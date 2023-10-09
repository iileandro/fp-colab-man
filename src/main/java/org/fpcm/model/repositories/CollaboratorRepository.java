package org.fpcm.model.repositories;

import org.fpcm.model.entities.Collaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollaboratorRepository extends JpaRepository<Collaborator, Integer> {

	@Query(value = "from Collaborator c left join fetch c.managedCollaborators where c.id = :id")
	Collaborator getWithChildren(int id);

	@Query(value = "from Collaborator c left join fetch c.managedCollaborators where c.managerId is null")
	List<Collaborator> showTree();

}