package lopez.rafael.mobileappws.security;

import lopez.rafael.mobileappws.SpringApplicationContext;

public class SecurityConstants {
    public final static long EXPIRATION_TIME = 864000000; //10 days
    public final static String TOKEN_PREFIX = "Bearer ";
    public final static String HEADER_STRING = "Authorization";
    public final static String SIGN_UP_URL = "/users";

    public static String getTokenSecret(){
        AppProperties properties = (AppProperties)SpringApplicationContext.getBean("AppProperties");
        return properties.getTokenSecret();
    }
}
