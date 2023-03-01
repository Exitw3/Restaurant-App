package com.codegym.repository;

import com.codegym.model.LocationRegion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRegionRepositiry extends JpaRepository<LocationRegion, Long> {
}
