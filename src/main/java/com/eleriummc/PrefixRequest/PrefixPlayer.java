package com.eleriummc.PrefixRequest;

public class PrefixPlayer {
    private String name;
    private String prefix;
    private String uuid;

    public PrefixPlayer(String name, String prefix, String uuid) {
        this.name = name;
        this.prefix = prefix;
        this.uuid = uuid;
    }

    public String getName() { return this.name; }

    public String getPrefix() { return this.prefix; }

    public String getUUID() { return this.uuid; }
}
