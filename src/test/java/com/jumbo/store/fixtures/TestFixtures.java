package com.jumbo.store.fixtures;

import com.jumbo.store.config.TestConfig;
import java.util.Arrays;
import java.util.List;

public class TestFixtures {

    public static class Jwt {

        public static final String WITHOUT_ROLES_AND_PERMISSIONS =
                TestConfig.createTestJwt("test-user", List.of(), List.of());

        public static final String WITH_READ_STORE_PERMISSION = getFakeJwtValueWithSpecificPermissions("read:store");

        private Jwt() {}

        public static class Stores {

            public static final String READ = getFakeJwtValueWithSpecificPermissions("read:store");
        }

        public static String getFakeJwtValueWithSpecificPermissions(String... permissions) {
            return TestConfig.createTestJwt("test-user", List.of("ROLE_CUSTOMER"), Arrays.asList(permissions));
        }
    }
}
