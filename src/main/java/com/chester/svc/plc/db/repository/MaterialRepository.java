package com.chester.svc.plc.db.repository;

import com.chester.svc.plc.db.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Material getByMaterialCodeAndAoCode(String material,String aoCode);
}
