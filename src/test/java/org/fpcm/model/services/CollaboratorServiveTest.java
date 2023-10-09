package org.fpcm.model.services;

import org.fpcm.model.entities.Collaborator;
import org.fpcm.model.repositories.CollaboratorRepository;
import org.fpcm.util.passwordmeter.PasswordCheckOptions;
import org.fpcm.util.passwordmeter.PasswordCheckResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CollaboratorServiveTest {

	@Mock
	private CollaboratorRepository collaboratorRepository;

	@Mock
	private PasswordService passwordService;

	@InjectMocks
	private CollaboratorService collaboratorService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInsertCollaborator() {
		Collaborator collaborator = new Collaborator();
		collaborator.setName("name");
		collaborator.setPlainPassword("password");

		PasswordCheckResult passwordCheckResult = new PasswordCheckResult("password", new PasswordCheckOptions());
		passwordCheckResult.setScore(5);

		when(passwordService.check("password")).thenReturn(passwordCheckResult);
		when(collaboratorRepository.save(any(Collaborator.class))).thenReturn(collaborator);
		when(collaboratorRepository.saveAndFlush(any(Collaborator.class))).thenReturn(collaborator);

		Collaborator result = collaboratorService.insert(collaborator);

		verify(passwordService).check("password");
		verify(collaboratorRepository).save(any(Collaborator.class));

		assertEquals(5, result.getPasswordScore());
	}

	@Test
	public void testUpdateCollaborator() {
		Collaborator collaboratorFromForm = new Collaborator();
		collaboratorFromForm.setId(1);
		collaboratorFromForm.setName("name");
		collaboratorFromForm.setPlainPassword("password");

		Collaborator existingCollaborator = new Collaborator();
		existingCollaborator.setId(1);
		existingCollaborator.setName("name");
		existingCollaborator.setPlainPassword("password");

		PasswordCheckResult passwordCheckResult = new PasswordCheckResult("password", new PasswordCheckOptions());
		passwordCheckResult.setScore(5);

		when(passwordService.check("password")).thenReturn(passwordCheckResult);
		when(collaboratorRepository.findById(1)).thenReturn(Optional.of(existingCollaborator));
		when(collaboratorRepository.save(any(Collaborator.class))).thenReturn(existingCollaborator);
		when(collaboratorRepository.saveAndFlush(any(Collaborator.class))).thenReturn(existingCollaborator);

		Collaborator result = collaboratorService.update(collaboratorFromForm);

		verify(collaboratorRepository).findById(1);
		verify(collaboratorRepository).saveAndFlush(any(Collaborator.class));

		assertEquals(existingCollaborator.getId(), result.getId());
	}

	@Test
	public void testDeleteCollaborator() {
		int id = 1;

		Collaborator collaborator = new Collaborator();
		collaborator.setId(id);

		when(collaboratorRepository.findById(id)).thenReturn(Optional.of(collaborator));

		boolean deleted = collaboratorService.delete(id);

		verify(collaboratorRepository).findById(id);
		verify(collaboratorRepository).delete(collaborator);

		assertTrue(deleted);
	}
}
