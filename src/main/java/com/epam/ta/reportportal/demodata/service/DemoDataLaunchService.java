/*
 * Copyright 2018 EPAM Systems
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

package com.epam.ta.reportportal.demodata.service;

import com.epam.ta.reportportal.auth.ReportPortalUser;
import com.epam.ta.reportportal.dao.LaunchRepository;
import com.epam.ta.reportportal.dao.TestItemRepository;
import com.epam.ta.reportportal.entity.enums.StatusEnum;
import com.epam.ta.reportportal.entity.launch.Launch;
import com.epam.ta.reportportal.exception.ReportPortalException;
import com.epam.ta.reportportal.ws.converter.builders.LaunchBuilder;
import com.epam.ta.reportportal.ws.model.ItemAttributeResource;
import com.epam.ta.reportportal.ws.model.launch.Mode;
import com.epam.ta.reportportal.ws.model.launch.StartLaunchRQ;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;

import static com.epam.ta.reportportal.entity.enums.StatusEnum.PASSED;
import static com.epam.ta.reportportal.ws.model.ErrorType.LAUNCH_NOT_FOUND;

/**
 * @author <a href="mailto:ihar_kahadouski@epam.com">Ihar Kahadouski</a>
 */
@Service
public class DemoDataLaunchService {

	private final LaunchRepository launchRepository;

	private final TestItemRepository testItemRepository;

	@Autowired
	public DemoDataLaunchService(LaunchRepository launchRepository, TestItemRepository testItemRepository) {
		this.launchRepository = launchRepository;
		this.testItemRepository = testItemRepository;
	}

	@Transactional
	public Long startLaunch(String name, int i, ReportPortalUser user, ReportPortalUser.ProjectDetails projectDetails) {
		StartLaunchRQ rq = new StartLaunchRQ();
		rq.setMode(Mode.DEFAULT);
		rq.setDescription(ContentUtils.getLaunchDescription());
		rq.setName(name);
		rq.setStartTime(new Date());
		Set<ItemAttributeResource> attributes = Sets.newHashSet(
				new ItemAttributeResource("platform", "desktop"),
				new ItemAttributeResource(null, "demo"),
				new ItemAttributeResource("build", "3.0.1." + i)
		);

		Launch launch = new LaunchBuilder().addStartRQ(rq)
				.addAttributes(attributes)
				.addProject(projectDetails.getProjectId())
				.addUser(user.getUserId())
				.get();
		launchRepository.save(launch);
		return launch.getId();
	}

	@Transactional
	public void finishLaunch(Long launchId, ReportPortalUser user, ReportPortalUser.ProjectDetails projectDetails) {
		Launch launch = launchRepository.findById(launchId)
				.orElseThrow(() -> new ReportPortalException(LAUNCH_NOT_FOUND, launchId.toString()));

		if (testItemRepository.hasItemsInStatusByLaunch(launchId, StatusEnum.IN_PROGRESS)) {
			testItemRepository.interruptInProgressItems(launchId);
		}

		launch = new LaunchBuilder(launch).addEndTime(new Date()).get();

		StatusEnum fromStatisticsStatus = PASSED;
		if (launchRepository.identifyStatus(launchId)) {
			fromStatisticsStatus = StatusEnum.FAILED;
		}
		launch.setStatus(fromStatisticsStatus);

		launchRepository.save(launch);
	}
}