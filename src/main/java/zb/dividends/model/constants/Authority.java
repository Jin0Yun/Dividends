package zb.dividends.model.constants;

public enum Authority {
    ROLE_READ("ROLE_READ"),
    ROLE_WRITE("ROLE_WRITE");

    private final String authority;

    Authority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
