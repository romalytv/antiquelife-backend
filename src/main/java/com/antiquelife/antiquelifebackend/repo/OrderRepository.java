package com.antiquelife.antiquelifebackend.repo;

import com.antiquelife.antiquelifebackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

}
