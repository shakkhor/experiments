package com.example.Itext.service;

import java.io.File;

public interface StorageService {
    void putFile(String key, byte[] bytes);
    byte[] getGetFile(String key);
}
