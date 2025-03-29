package ir.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue("ADMIN")
@Entity
public class Admin extends User {
}