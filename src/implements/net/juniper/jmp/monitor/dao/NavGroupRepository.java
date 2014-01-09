package net.juniper.jmp.monitor.dao;

import net.juniper.jmp.monitor.jpa.NavGroupEntity;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;
/**
 * Dao implementation for navgroup
 * @author juntaod
 *
 */
public interface NavGroupRepository extends Repository<NavGroupEntity, Integer> {
	Iterable<NavGroupEntity> findAll(Sort sort);
}
