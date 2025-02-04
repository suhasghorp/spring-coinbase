package org.example.springcoinbase.tasks;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.springcoinbase.model.Coin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;

/* WATCH YOUTUBE https://youtu.be/ZUcsd2xiRto?si=KUosFCVDgRAgcH68
Must define 3 environment variables starrting with AZURE_
 */

@Component
@Slf4j
public class TwilioTask {

    //private final Environment env;
    private final Set<Coin> coins = new HashSet<>();
    @Value("${TWILIO_ACCOUNT_SID}")
    private String twilioAccountSIDSecret;
    @Value("${TWILIO_AUTH_TOKEN}")
    private String twilioAuthTokenSecret;
    @Value("${TWILIO_FROM_NUMBER}")
    private String twilioFromSecret;
    @Value("${TWILIO_TO_NUMBER}")
    private String twilioToSecret;
    //@Value("${AZURE_KEYVAULT_URL}")
    //private String keyVaultUrl;

    @PostConstruct
    public void init() {
        /*SecretClient secretClient = new SecretClientBuilder().vaultUrl(keyVaultUrl)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        String twilioAccountSID = "TWILIO-ACCOUNT-SID";
        twilioAccountSIDSecret = secretClient.getSecret(twilioAccountSID).getValue();
        String twilioAuthToken = "TWILIO-AUTH-TOKEN";
        twilioAuthTokenSecret = secretClient.getSecret(twilioAuthToken).getValue();
        String twilioFrom = "TWILIO-FROM-NUMBER";
        twilioFromSecret = secretClient.getSecret(twilioFrom).getValue();
        String twilioTo = "TWILIO-TO-NUMBER";
        twilioToSecret = secretClient.getSecret(twilioTo).getValue();*/
    }
    @Scheduled(fixedDelay = 5000, initialDelay = 5000)
    public void call() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Price Alert!");
            if (!coins.isEmpty()) {

                for (Coin c : coins) {
                    if (c.getPrice() > c.getHighThreshold()) {
                        sb.append(String.format("The price of %s has risen to %.2f which is higher than %.2f.",
                                c.getSymbol(), c.getPrice(), c.getHighThreshold()));
                    } else if (c.getPrice() < c.getLowThreshold()) {
                        sb.append(String.format("The price of %s has dropped to %.2f which is lower than %.2f.",
                                c.getSymbol(), c.getPrice(), c.getLowThreshold()));
                    }
                }
                assert twilioAccountSIDSecret != null;
                assert twilioAuthTokenSecret != null;
                Twilio.init(twilioAccountSIDSecret, twilioAuthTokenSecret);
                Call call = Call.creator(new PhoneNumber(twilioToSecret), new PhoneNumber(twilioFromSecret),
                        new com.twilio.type.Twiml("<Response><Say>" + sb + "</Say></Response>")).create();
                log.info("Twilio call returned : {}", call.getSid());
                coins.clear();
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    public void addCoin(Coin coin) {
        coins.add(coin);
    }
}
