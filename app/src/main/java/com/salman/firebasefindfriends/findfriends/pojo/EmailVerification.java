package com.salman.firebasefindfriends.findfriends.pojo;




public class EmailVerification {


    private String email;
    private String did_you_mean;
    private boolean mx_found;
    private boolean smtp_check;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDid_you_mean() {
        return did_you_mean;
    }

    public void setDid_you_mean(String did_you_mean) {
        this.did_you_mean = did_you_mean;
    }

    public boolean isMx_found() {
        return mx_found;
    }

    public void setMx_found(boolean mx_found) {
        this.mx_found = mx_found;
    }

    public boolean isSmtp_check() {
        return smtp_check;
    }

    public void setSmtp_check(boolean smtp_check) {
        this.smtp_check = smtp_check;
    }
}
