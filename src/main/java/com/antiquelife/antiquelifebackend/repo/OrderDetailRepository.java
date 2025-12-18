package com.antiquelife.antiquelifebackend.repo;

import com.antiquelife.antiquelifebackend.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {

}