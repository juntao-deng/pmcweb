package net.juniper.jmp.monitor.dao;

import net.juniper.jmp.monitor.jpa.ServerEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
/**
 * Dao implementation for server
 * @author juntaod
 *
 */
public interface ServerRepository extends JpaRepository<ServerEntity, Integer>, JpaSpecificationExecutor<ServerEntity>{
}
