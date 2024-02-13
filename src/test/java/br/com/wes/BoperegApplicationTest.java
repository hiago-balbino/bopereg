package br.com.wes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class BoperegApplicationTest {

    @Test
    @DisplayName("Should guarantee that run application method is called")
    public void shouldGuaranteeThatRunApplicationIsCalled() {
        try (MockedStatic<SpringApplication> applicationMock = mockStatic(SpringApplication.class)) {
            BoperegApplication.main(new String[]{});
            applicationMock.verify(() -> SpringApplication.run(BoperegApplication.class, new String[]{}));
            applicationMock.verifyNoMoreInteractions();
        }
    }

}