package com.demo.kafkaProducer.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.demo.kafkaProducer.domain.Producer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProducerDTO implements Serializable {

    private Long id;

    private String ownerName;

    private String productName;

    private Integer quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProducerDTO)) {
            return false;
        }

        ProducerDTO producerDTO = (ProducerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, producerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProducerDTO{" +
            "id=" + getId() +
            ", ownerName='" + getOwnerName() + "'" +
            ", productName='" + getProductName() + "'" +
            ", quantity=" + getQuantity() +
            "}";
    }
}
