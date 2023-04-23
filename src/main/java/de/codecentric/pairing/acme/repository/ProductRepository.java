package de.codecentric.pairing.acme.repository;

import de.codecentric.pairing.acme.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
