package com.rs.multirediscacheservice.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "M_POLICY")
@Data
public class Policy implements Serializable {

    @EmbeddedId
    private PolicyKey policyKey;

    @Column(name = "POLICY_HOLDER_NAME")
    private String policyHolderName;

    @Column(name = "POLICY_HOLDER_PHONE")
    private String policyHolderPhone;
}
