package dev.carlaum.home.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Type {

    PRIVATE(0, "Privada"), PUBLIC(1, "Pública");

    private final int id;
    private final String name;

    public static Type fromId(int id) {
        return Arrays.stream(Type.values()).filter(x -> x.getId() == id).findAny().orElse(null);
    }
}