package com.passwordmanager;

public class DataBlock {
    private final byte[] data;
    private final byte[] iv;

    public DataBlock(byte[] data, byte[] iv){
        this.data=data;
        this.iv=iv;
    }

    public byte[] getData(){
        return data;
    }

    public byte[] getIv() {
        return iv;
    }
}
