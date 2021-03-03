package edu.leicester.co2103.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;

public interface ModuleRepository extends CrudRepository<Module, String> {

    @Override
    List<Module> findAll();
	
}
