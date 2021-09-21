package com.greglturnquist.hackingspringboot.reactive;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * 사이트 초기 홈 화면을 보여주는 단순한 홈 컨트롤러
 * 스프링 시큐리티 인증 적용
 * Created by KMS on 2021/09/11.
 */
@Controller
public class HomeController {

    private final InventoryService inventoryService;

    public HomeController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * 사용자별 장바구니 작성
     * @param authorizedClient
     * @param oauth2User
     * @return
     */
    @GetMapping
    // 단순히 Authentication 객체를 가져오는 대신에 OAuth2AuthorizedClient와 OAuth2User를 주입받는다
    // OAuth2AuthorizedClient에는 OAuth 클라이언트 정보가 담겨 있고, OAuth2User에는 로그인한 사용자 정보가 담겨 있다
    // @RegistereOAuth2AuthorizedClient와 @AuthenticationPrincipal 애너테이션은 컨트롤러 메소드의 파라미터에 붙어서
    // 스프링 스키류티가 컨트롤러 메소드의 파라미터 값을 결정하는 데 사용된다
    Mono<Rendering> home(
            @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OAuth2User oauth2User) {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", this.inventoryService.getInventory())
                // 장바구니 이름을 알아낼 때 Authentication 타입이 아니라 OAuth2User 타입을 인자로 받는 cartName() 메소드가 사용됐다
                // 따라서 cartName() 메소드도 따라서 변경돼야 할 것이다
                .modelAttribute("cart", this.inventoryService.getCart(cartName(oauth2User))
                        .defaultIfEmpty(new Cart(cartName(oauth2User))))
                // Authentication 객체를 템플릿에 모델로 제공해주면, 템플릿이 웹 페이지의 컨텍스트에
                // 모델 데이터를 담아서 사용할 수 있게 된다
                .modelAttribute("userName", oauth2User.getName()) //
                .modelAttribute("authorities", oauth2User.getAuthorities()) //
                .modelAttribute("clientName", //
                        authorizedClient.getClientRegistration().getClientName()) //
                .modelAttribute("userAttributes", oauth2User.getAttributes()) //
                .build());
    }

    /**
     * Item 추가
     * @param oauth2User
     * @param id
     * @return
     */
    @PostMapping("/add/{id}")
    Mono<String> addToCart(@AuthenticationPrincipal OAuth2User oauth2User, @PathVariable String id) {
        return this.inventoryService.addItemToCart(cartName(oauth2User), id) //
                .thenReturn("redirect:/");
    }

    /**
     * Item 삭제
     * @param oauth2User
     * @param id
     * @return
     */
    @DeleteMapping("/remove/{id}")
    Mono<String> removeFromCart(@AuthenticationPrincipal OAuth2User oauth2User, @PathVariable String id) {
        return this.inventoryService.removeOneFromCart(cartName(oauth2User), id) //
                .thenReturn("redirect:/");
    }

    @PostMapping
    @ResponseBody
    Mono<Item> createItem(@RequestBody Item newItem) {
        return this.inventoryService.saveItem(newItem);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    Mono<Void> deleteItem(@PathVariable String id) {
        return this.inventoryService.deleteItem(id);
    }

    /**
     * OAuth2 사용자 기준으로 장바구니 이름을 알아내도록 수정
     * @param oAuth2User
     * @return
     */
    private static String cartName(OAuth2User oAuth2User) {
        return oAuth2User.getName() + "'s Cart";
    }
}