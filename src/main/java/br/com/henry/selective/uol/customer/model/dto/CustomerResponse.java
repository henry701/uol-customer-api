package br.com.henry.selective.uol.customer.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerResponse {

    private Long id;

    private String name;

    private Integer age;

}
