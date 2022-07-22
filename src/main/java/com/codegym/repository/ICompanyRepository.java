package com.codegym.repository;

import com.codegym.model.entity.Company;

import java.util.Optional;

import com.codegym.model.entity.RecruitmentNews;
import com.codegym.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findAll(Pageable pageable);

    //danh sach cong ty duoc de xuat
    @Query(value = "select * from companies where proposed = 0;", nativeQuery = true)
    Iterable<Company> findAllProposedCompanies();

    //danh sach cong ty cho duyet
    @Query(value = "select * from companies where approval = 1;", nativeQuery = true)
    Iterable<Company> findAllPendingCompanies();


    //danh sach cong ty da duyet
    @Query(value = "select * from companies where approval = 0;", nativeQuery = true)
    Iterable<Company> findAllApprovedCompanies();

    //danh sach cong ty khong khoa
    @Query(value = "select * from companies where status = 1;", nativeQuery = true)
    Iterable<Company> findAllUnlockCompanies();

    //danh sach cong ty bi khoa
    @Query(value = "select * from companies where status = 0;", nativeQuery = true)
    Iterable<Company> findAllLockCompanies();

    //tim kiem co ton tai trong DB khong
    Optional<Company> findByName(String name);

    Optional<Company> findByEmail(String email);

    //kt xem user da co tong DB chua khi tao du lieu
    Boolean existsByName(String name);

    //kt xem email da co torng DB chua khi tao du lieu
    Boolean existsByEmail(String email);
}
