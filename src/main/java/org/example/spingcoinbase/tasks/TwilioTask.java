package org.example.spingcoinbase.tasks;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.example.spingcoinbase.TelemetryLogger;
import org.example.spingcoinbase.model.Coin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;

/* WATCH YOUTUBE https://youtu.be/ZUcsd2xiRto?si=KUosFCVDgRAgcH68
Must define 3 environment variables starrting with AZURE_
 */

@Component
public class TwilioTask {

    //private final Environment env;
    private final Set<Coin> coins = new HashSet<>();
    private String twilioAccountSIDSecret;
    private String twilioAuthTokenSecret;
    private String twilioFromSecret;
    private String twilioToSecret;
    @Value("${AZURE_KEYVAULT_URL}")
    private String keyVaultUrl;

    @PostConstruct
    public void init() {
        SecretClient secretClient = new SecretClientBuilder().vaultUrl(keyVaultUrl)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        String twilioAccountSID = "TWILIO-ACCOUNT-SID";
        twilioAccountSIDSecret = secretClient.getSecret(twilioAccountSID).getValue();
        String twilioAuthToken = "TWILIO-AUTH-TOKEN";
        twilioAuthTokenSecret = secretClient.getSecret(twilioAuthToken).getValue();
        String twilioFrom = "TWILIO-FROM-NUMBER";
        twilioFromSecret = secretClient.getSecret(twilioFrom).getValue();
        String twilioTo = "TWILIO-TO-NUMBER";
        twilioToSecret = secretClient.getSecret(twilioTo).getValue();
    }
    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void call() {
        try {
            String text = "Price Alert!";
            if (!coins.isEmpty()) {
                for (Coin c : coins) {
                    text += String.format("The price of %s has dropped to %.2f which is lower than %.2f.",
                            c.getSymbol(), c.getPrice(), c.getThreshold());
                }

                Twilio.init(twilioAccountSIDSecret, twilioAuthTokenSecret);
                Call call = Call.creator(new PhoneNumber(twilioToSecret), new PhoneNumber(twilioFromSecret),
                        new com.twilio.type.Twiml("<Response><Say>" + text + "</Say></Response>")).create();
                TelemetryLogger.info(call.getSid());
                //System.out.println(response.body());
                coins.clear();
            }
        } catch (Exception e) {
            TelemetryLogger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void addCoin(Coin coin) {
        coins.add(coin);
    }
}
