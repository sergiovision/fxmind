package com.fxmind.repository;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

/** Fast feedback: runs the repository contract on in-memory H2 in MySQL compatibility mode. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class H2RepositoryTest extends AbstractRepositoryTest {}
