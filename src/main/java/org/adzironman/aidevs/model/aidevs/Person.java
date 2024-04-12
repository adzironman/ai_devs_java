package org.adzironman.aidevs.model.aidevs;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Person {
    public String imie;
    public String nazwisko;
    public String o_mnie;
    public String ulubiony_kolor;
}