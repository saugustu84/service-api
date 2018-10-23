package com.epam.ta.reportportal.core.widget.content.loader;

import com.epam.ta.reportportal.commons.querygen.Condition;
import com.epam.ta.reportportal.commons.querygen.Filter;
import com.epam.ta.reportportal.commons.querygen.FilterCondition;
import com.epam.ta.reportportal.commons.validation.BusinessRule;
import com.epam.ta.reportportal.core.widget.content.LoadContentStrategy;
import com.epam.ta.reportportal.dao.LaunchRepository;
import com.epam.ta.reportportal.dao.WidgetContentRepository;
import com.epam.ta.reportportal.entity.launch.Launch;
import com.epam.ta.reportportal.entity.widget.content.FlakyCasesTableContent;
import com.epam.ta.reportportal.entity.widget.content.LatestLaunchContent;
import com.epam.ta.reportportal.exception.ReportPortalException;
import com.epam.ta.reportportal.ws.model.ErrorType;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.epam.ta.reportportal.commons.Predicates.equalTo;
import static com.epam.ta.reportportal.core.widget.content.constant.ContentLoaderConstants.*;
import static com.epam.ta.reportportal.core.widget.util.WidgetFilterUtil.GROUP_FILTERS;
import static com.epam.ta.reportportal.dao.constant.WidgetContentRepositoryConstants.NAME;

/**
 * @author Ivan Budayeu
 */
@Service
public class FlakyCasesTableContentLoader implements LoadContentStrategy {

	@Autowired
	private WidgetContentRepository widgetRepository;

	@Autowired
	private LaunchRepository launchRepository;

	@Override
	public Map<String, ?> loadContent(List<String> contentFields, Map<Filter, Sort> filterSortMapping, Map<String, String> widgetOptions,
			int limit) {

		validateWidgetOptions(widgetOptions);

		validateFilterSortMapping(filterSortMapping);

		Filter filter = GROUP_FILTERS.apply(filterSortMapping.keySet());

		String launchName = widgetOptions.get(LAUNCH_NAME_FIELD);

		Launch launch = launchRepository.findLatestByNameAndFilter(launchName, filter)
				.orElseThrow(() -> new ReportPortalException(ErrorType.LAUNCH_NOT_FOUND, "No launch with name: " + launchName));

		filter.withCondition(new FilterCondition(Condition.EQUALS, false, launch.getName(), NAME));

		LatestLaunchContent latestLaunchContent = new LatestLaunchContent(launch);

		List<FlakyCasesTableContent> flakyCasesTableContent = widgetRepository.flakyCasesStatistics(filter, limit);

		return ImmutableMap.<String, Object>builder().put(LATEST_LAUNCH, latestLaunchContent).put(FLAKY, flakyCasesTableContent).build();
	}

	/**
	 * Mapping should not be empty
	 *
	 * @param filterSortMapping Map of ${@link Filter} for query building as key and ${@link Sort} as value for each filter
	 */
	private void validateFilterSortMapping(Map<Filter, Sort> filterSortMapping) {
		BusinessRule.expect(MapUtils.isNotEmpty(filterSortMapping), equalTo(true))
				.verify(ErrorType.BAD_REQUEST_ERROR, "Filter-Sort mapping should not be empty");
	}

	/**
	 * Validate provided widget options. For current widget launch name should be specified.
	 *
	 * @param widgetOptions Map of stored widget options.
	 */
	private void validateWidgetOptions(Map<String, String> widgetOptions) {
		BusinessRule.expect(MapUtils.isNotEmpty(widgetOptions), equalTo(true))
				.verify(ErrorType.BAD_REQUEST_ERROR, "Widget options should not be null.");

		String launchName = widgetOptions.get(LAUNCH_NAME_FIELD);
		BusinessRule.expect(launchName, StringUtils::isNotEmpty)
				.verify(ErrorType.UNABLE_LOAD_WIDGET_CONTENT, LAUNCH_NAME_FIELD + " should be specified for widget.");
	}
}