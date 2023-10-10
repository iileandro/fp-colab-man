package org.fpcm.model.services;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fpcm.model.entities.Collaborator;
import org.fpcm.model.repositories.CollaboratorRepository;
import org.fpcm.util.passwordmeter.PasswordCheckResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CollaboratorService {

	private final CollaboratorRepository repository;
	private final PasswordService passwordService;

	public List<Collaborator> showTree() {
		return repository.showTree();
	}

	@Transactional(readOnly = true)
	public Collaborator get(int id) {
		return repository.getWithChildren(id);
	}

	@Transactional
	public Collaborator insert(Collaborator collaborator){
		saveAndFlushToGetID(collaborator);
		proccessPassword(collaborator);

		if(collaborator.getManagerId() != null && collaborator.getManagerId() > 0){
			collaborator.setManager(findById(collaborator.getManagerId()));
		}
		proccessTreePath(collaborator);

		collaborator = repository.save(collaborator);
		collaborator.setManagedCollaborators(null);
		return collaborator;
	}

	@Transactional
	public Collaborator update(Collaborator collaboratorFromForm) {
		Collaborator collaborator = findById(collaboratorFromForm.getId());
		collaborator.fillFromUpdate(collaboratorFromForm);
		saveAndFlushToGetID(collaborator);
		if(!StringUtils.isEmpty(collaborator.getPlainPassword())){
			proccessPassword(collaborator);
		}
		return repository.save(collaborator);
	}

	@Transactional
	public boolean delete(int id) {
		Collaborator collaborator = findById(id);
		try{
			repository.delete(collaborator);
		}catch (Exception ignored) {
			return false;
		}
		return true;
	}

	private Collaborator findById(int id) {
		return repository.findById(id).orElseThrow();
	}


	private void saveAndFlushToGetID(Collaborator collaborator) {
		repository.saveAndFlush(collaborator); // so we'll be able to get id in the next routine when inserting.
	}

	private void proccessTreePath(Collaborator collaborator) {
		if(collaborator.getManager()!= null){
			collaborator.setTreePath(collaborator.getManager().getTreePath() + collaborator.getManager().getId()+"/");
		}else{
			collaborator.setTreePath("/");
		}
	}

	private void proccessPassword(Collaborator collaborator) {
		PasswordCheckResult result = passwordService.check(collaborator.getPlainPassword());
		collaborator.setPasswordScore(result.getScore());
		collaborator.setEncryptedPassword(passwordService.encryptFor(collaborator));
	}
}
