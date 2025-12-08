package tmdt.com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    private Integer sellerId;

    @OneToOne
    @JoinColumn(name = "seller_id")
    private User user;

    private String shopName;
    private String shopDescription;
    private String shopLogo;

    private Double rating;
    private Integer totalReview;
}

