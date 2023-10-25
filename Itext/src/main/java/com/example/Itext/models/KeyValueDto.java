package com.example.Itext.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyValueDto {
    private String key;
    private String value;
    private boolean enable;
    private int order;
}
