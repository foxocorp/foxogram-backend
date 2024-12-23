package su.foxogram.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import su.foxogram.configs.APIConfig;
import su.foxogram.exceptions.code.CodeExpiredException;
import su.foxogram.exceptions.code.CodeIsInvalidException;
import su.foxogram.models.Code;
import su.foxogram.repositories.CodeRepository;

@Slf4j
@Service
public class CodeService {

	private final CodeRepository codeRepository;

	private final APIConfig apiConfig;

	public CodeService(CodeRepository codeRepository, APIConfig apiConfig) {
		this.codeRepository = codeRepository;
		this.apiConfig = apiConfig;
	}

	public Code validateCode(String pathCode) throws CodeIsInvalidException, CodeExpiredException {
		Code code = codeRepository.findByValue(pathCode);

		if (apiConfig.isDevelopment()) return null;

		if (code == null)
			throw new CodeIsInvalidException();

		if (code.expiresAt <= System.currentTimeMillis())
			throw new CodeExpiredException();

		log.info("Code ({}) for user ({}) validated successfully", code.getValue(), code.getUserId());

		return code;
	}

	public void deleteCode(Code code) {
		if (!apiConfig.isDevelopment()) codeRepository.delete(code);
		log.info("Code ({}, {}) deleted successfully", code.getValue(), code.getUserId());
	}

	public void saveCode(long id, String type, String digitCode, long issuedAt, long expiresAt) {
		Code code = new Code(id, type, digitCode, issuedAt, expiresAt);
		if (!apiConfig.isDevelopment()) codeRepository.save(code);
		log.info("Code ({}, {}) saved successfully", code.getValue(), code.getUserId());
	}
}
