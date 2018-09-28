/*
 * Copyright 2017 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/reportportal/service-api
 *
 * Report Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Report Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.epam.ta.reportportal.core.dashboard;

import com.epam.ta.reportportal.auth.ReportPortalUser;
import com.epam.ta.reportportal.exception.ReportPortalException;
import com.epam.ta.reportportal.ws.model.SharedEntity;
import com.epam.ta.reportportal.ws.model.dashboard.DashboardResource;
import org.springframework.data.domain.Pageable;

/**
 * Get dashboard handler.
 *
 * @author Aliaksei_Makayed
 */
public interface IGetDashboardHandler {

	/**
	 * Get dashboard by id
	 *
	 * @param dashboardId    Dashboard id
	 * @param projectDetails Project details
	 * @param user           User
	 * @return {@link DashboardResource}
	 */
	DashboardResource getDashboard(Long dashboardId, ReportPortalUser.ProjectDetails projectDetails, ReportPortalUser user);

	/**
	 * Get all dashboards.
	 *
	 * @param projectDetails Project details
	 * @param user           User
	 * @return {@link Iterable}
	 * @throws ReportPortalException
	 */
	Iterable<DashboardResource> getAllDashboards(ReportPortalUser.ProjectDetails projectDetails, ReportPortalUser user);

	/**
	 * Get dashboards names shared for current project.
	 * Result map:<br>
	 * <li>key - dashboard id,
	 * <li>value - dashboard name
	 *
	 * @param projectName
	 * @return {@link Iterable}
	 * @throws ReportPortalException
	 */
	Iterable<SharedEntity> getSharedDashboardsNames(String ownerName, String projectName, Pageable pageable);
}