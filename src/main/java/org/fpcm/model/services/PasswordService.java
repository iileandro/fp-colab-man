package org.fpcm.model.services;

import org.fpcm.model.entities.Collaborator;
import org.fpcm.util.HashUtils;
import org.fpcm.util.passwordmeter.PasswordCheckResult;
import org.fpcm.util.passwordmeter.PasswordChecker;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

	private static final String PSWD_SALT = "LXiOdmlGvTALI4H3pyVC4hIp5BVcZZZ3MBeYyoH8YHsIm9rACu0zpRPxyUTcdpSw";

	public PasswordCheckResult check(String password){
		return PasswordChecker.getInstance().execute(password);
	}

	public String encryptFor(Collaborator collaborator) {
		assert collaborator !=null && collaborator.getPlainPassword() != null;
		return HashUtils.md5(PSWD_SALT + collaborator.getPlainPassword() + collaborator.getId());
	}
}
