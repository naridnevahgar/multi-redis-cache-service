package com.rs.multirediscacheservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyKey implements Serializable {

    @Column(name = "L_PROD_CD", nullable = false)
    private String productCode;

    @Column(name = "POLICY_NUM", nullable = false)
    private String policyNumber;
}
