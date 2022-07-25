package com.codegym.service.recruitment_news;

import com.codegym.model.entity.Company;
import com.codegym.model.entity.RecruitmentNews;
import com.codegym.service.IGeneralService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IRecruitmentNewsService extends IGeneralService<RecruitmentNews> {
    Iterable<RecruitmentNews> findAllProposedRecruitmentNews();

    Iterable<RecruitmentNews> findAllByCompany(Company company);

    //danh sach tin tuyen dung bi khoa
    Iterable<RecruitmentNews> findAllLockedRecruitmentNews();

    // danh sach tuyen dung khong khoa
    Iterable<RecruitmentNews> findAllUnlockRecruitmentNews();

    Page<RecruitmentNews> findPageUnlockRecruitmentNews(Pageable pageable);

    //tim theo title, dia diem
    Iterable<RecruitmentNews> findAllByTitleContainingAndWorkingPlace(String title, String place);

    //tim kiem theo title
    Iterable<RecruitmentNews> findAllByTitleContaining(String title);

    //tìm theo job title, salary range, kinh nghiệm, thành phố
    Iterable<RecruitmentNews> findAllByTitleSalaryExperiencePlace(@Param("title") String title,
                                                                  @Param("from") int salary_from,
                                                                  @Param("to") int salary_to,
                                                                  @Param("experience") int required_experience,
                                                                  @Param("place") String working_place);
}
