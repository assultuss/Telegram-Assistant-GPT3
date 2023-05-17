package com.example.assassistant.service.telegram.skill.action;

import com.example.assassistant.domain.CryptoPrice;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
public class GetCryptoPriceAction implements SkillAction<CryptoPrice> {
    private final WebClient binanceClient;

    /**
     * @param onCryptoPrice Telegram bot API skill to perform when skill skill is executed.
     *                    It will be called after skill skill execution.
     * @return the skill, that will be executed if parsed GPT-3 response has an asset parameter.
     */
    @Override
    public Consumer<String> doWithResult(Consumer<CryptoPrice> onCryptoPrice) {
        return asset -> {
            Function<UriBuilder, URI> uri = uriBuilder -> uriBuilder
                    .queryParam("symbol", asset + "USDT")
                    .build();

            binanceClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(CryptoPrice.class)
                    .subscribe(onCryptoPrice);
        };

    }
}
