package com.codegym.service.vacancy;

import com.codegym.model.entity.Vacancy;
import com.codegym.repository.IVacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VacancyService implements IVacancyService {
    @Autowired
    private IVacancyRepository vacancyRepository;

    @Override
    public Iterable<Vacancy> findAll() {
        return null;
    }

    @Override
    public Optional<Vacancy> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        return null;
    }

    @Override
    public void remove(Long id) {

    }
}
