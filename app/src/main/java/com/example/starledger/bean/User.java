package com.example.starledger.bean;

public class User {
    private int id;
    private String account;
    private String password;
    private String gxmsg; // 个性签名
    private int history_gold = 0; // 获得的积分
    private int gold = 0; // 当前积分
    private int limit = 200; // 每日消费上限

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(int id, String account, String password, String gxmsg, int history_gold, int gold, int limit) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.gxmsg = gxmsg;
        this.history_gold = history_gold;
        this.gold = gold;
        this.limit = limit;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getGxmsg() { return gxmsg; }
    public void setGxmsg(String gxmsg) { this.gxmsg = gxmsg; }

    public int getHistory_gold() { return history_gold; }
    public void setHistory_gold(int history_gold) { this.history_gold = history_gold; }

    public int getGold() { return gold; }
    public void setGold(int gold) { this.gold = gold; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}
