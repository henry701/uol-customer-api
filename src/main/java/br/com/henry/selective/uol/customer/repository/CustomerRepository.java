package br.com.henry.selective.uol.customer.repository;

import br.com.henry.selective.uol.customer.model.entity.Customer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {
    @CacheEvict(value = "customers", key = "#root.args[0].id")
    @Override
    <S extends Customer> S save(S entity);

    @CacheEvict(value = "customers", allEntries = true)
    @Override
    <S extends Customer> Iterable<S> saveAll(Iterable<S> entities);

    @Cacheable(value = "customers")
    @Override
    Optional<Customer> findById(Long aLong);

    @Cacheable(value = "customers")
    @Override
    boolean existsById(Long aLong);

    @CacheEvict(value = "customers")
    @Override
    void deleteById(Long aLong);

    @CacheEvict(value = "customers", key = "#root.args[0].id")
    @Override
    void delete(Customer entity);

    @CacheEvict(allEntries = true)
    @Override
    void deleteAll(Iterable<? extends Customer> entities);

    @CacheEvict(allEntries = true)
    @Override
    void deleteAll();
}