package com.bankymono.authorization_server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.List;


@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${password}")
    private String password;

    @Value("${privateKey}")
    private String privateKey;

    @Value("${alias}")
    private String alias;


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Value("${jwt.key}")
    private String jwtKey;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        var tokenEnhancers = List.of(new CustomTokenEnhancer(),
                jwtAccessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain);
//                .accessTokenConverter(jwtAccessTokenConverter());
    }

    @Bean
    public TokenStore tokenStore() {
//        return new JdbcTokenStore(dataSource);
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        var converter = new JwtAccessTokenConverter();
//        converter.setSigningKey(jwtKey);

        KeyStoreKeyFactory keyStoreKeyFactory
                = new KeyStoreKeyFactory(
                        new ClassPathResource(privateKey),
                password.toCharArray()
        );
        converter.setKeyPair(
                keyStoreKeyFactory.getKeyPair(alias)
        );
        return converter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
//        var service = new InMemoryClientDetailsService();
//        var cd = new BaseClientDetails();
//        cd.setClientId("client");
//        cd.setClientSecret("secret");
//        cd.setScope(List.of("read"));
//        cd.setAuthorizedGrantTypes(List.of("password"));
//        service.setClientDetailsStore(
//                Map.of("client", cd));
//
//        clients.withClientDetails(service);

//        clients
//                .inMemory()
//                .withClient("client")
//                .secret("secret")
//                .authorizedGrantTypes("password")
//                .scopes("read");

        clients
                .inMemory()
                .withClient("client")
                .secret("secret")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read");
//                .and()
//                .withClient("resourceserver")
//                .secret("resourceserversecret");
//                .redirectUris("http://localhost:9090/home");

//        clients
//                .inMemory()
//                .withClient("client1")
//                .secret("secret2")
//                .authorizedGrantTypes("authorization_code")
//                .scopes("read")
//                .redirectUris("http://localhost:9090/home")
//                .and()
//                .withClient("client2")
//                .secret("secret2")
//                .authorizedGrantTypes("authorization_code","password","refresh_token")
//                .scopes("read")
//                .redirectUris("http://localhost:9090/home");
    }

//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        security.checkTokenAccess("isAuthenticated()");
//    }
}
