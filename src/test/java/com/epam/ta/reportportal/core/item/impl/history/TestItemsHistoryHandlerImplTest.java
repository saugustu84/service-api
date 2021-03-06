/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.ta.reportportal.core.item.impl.history;

import com.epam.ta.reportportal.ReportPortalUserUtil;
import com.epam.ta.reportportal.commons.ReportPortalUser;
import com.epam.ta.reportportal.dao.LaunchRepository;
import com.epam.ta.reportportal.dao.TestItemRepository;
import com.epam.ta.reportportal.entity.project.ProjectRole;
import com.epam.ta.reportportal.entity.user.UserRole;
import com.epam.ta.reportportal.exception.ReportPortalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.epam.ta.reportportal.util.ProjectExtractor.extractProjectDetails;
import static com.epam.ta.reportportal.ws.model.ValidationConstraints.MAX_HISTORY_DEPTH_BOUND;
import static com.epam.ta.reportportal.ws.model.ValidationConstraints.MIN_HISTORY_DEPTH_BOUND;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author <a href="mailto:ihar_kahadouski@epam.com">Ihar Kahadouski</a>
 */
@ExtendWith(MockitoExtension.class)
class TestItemsHistoryHandlerImplTest {

	@Mock
	private TestItemRepository testItemRepository;

	@Mock
	private LaunchRepository launchRepository;

	@InjectMocks
	private TestItemsHistoryHandlerImpl handler;

	@Test
	void historyDepthLowerThanBoundTest() {
		ReportPortalUser rpUser = ReportPortalUserUtil.getRpUser("test", UserRole.USER, ProjectRole.MEMBER, 1L);

		assertThrows(ReportPortalException.class,
				() -> handler.getItemsHistory(extractProjectDetails(rpUser, "test_project"),
						new Long[] { 1L, 2L },
						MIN_HISTORY_DEPTH_BOUND - 1,
						false
				)
		);
	}

	@Test
	void historyDepthGreaterThanBoundTest() {
		ReportPortalUser rpUser = ReportPortalUserUtil.getRpUser("test", UserRole.USER, ProjectRole.MEMBER, 1L);

		assertThrows(ReportPortalException.class,
				() -> handler.getItemsHistory(extractProjectDetails(rpUser, "test_project"),
						new Long[] { 1L, 2L },
						MAX_HISTORY_DEPTH_BOUND - 1,
						false
				)
		);
	}
}