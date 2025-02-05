/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.asset.service.persistence;

import com.liferay.portal.kernel.cache.Lifecycle;
import com.liferay.portal.kernel.cache.ThreadLocalCache;
import com.liferay.portal.kernel.cache.ThreadLocalCacheManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.AggregateTestRule;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.test.DeleteAfterTestRun;
import com.liferay.portal.test.LiferayIntegrationTestRule;
import com.liferay.portal.test.MainServletTestRule;
import com.liferay.portal.util.test.GroupTestUtil;
import com.liferay.portal.util.test.RandomTestUtil;
import com.liferay.portal.util.test.ServiceContextTestUtil;
import com.liferay.portal.util.test.TestPropsValues;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;
import com.liferay.portlet.asset.service.impl.AssetEntryServiceImpl;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.portlet.blogs.util.test.BlogsTestUtil;
import com.liferay.portlet.ratings.model.RatingsStats;
import com.liferay.portlet.ratings.service.RatingsEntryServiceUtil;
import com.liferay.portlet.ratings.service.RatingsStatsLocalServiceUtil;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Sergio González
 */
public class AssetEntryQueryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), MainServletTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		AssetVocabulary vocabulary =
			AssetVocabularyLocalServiceUtil.addVocabulary(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				serviceContext);

		_assetVocabularyId = vocabulary.getVocabularyId();

		AssetCategory fashionCategory =
			AssetCategoryLocalServiceUtil.addCategory(
				TestPropsValues.getUserId(), "Fashion", _assetVocabularyId,
				serviceContext);

		_fashionAssetCategoryId = fashionCategory.getCategoryId();

		AssetCategory foodCategory = AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), "Food", _assetVocabularyId,
			serviceContext);

		_foodAssetCategoryId = foodCategory.getCategoryId();

		AssetCategory healthCategory =
			AssetCategoryLocalServiceUtil.addCategory(
				TestPropsValues.getUserId(), "Health", _assetVocabularyId,
				serviceContext);

		_healthAssetCategoryId = healthCategory.getCategoryId();

		AssetCategory sportCategory = AssetCategoryLocalServiceUtil.addCategory(
			TestPropsValues.getUserId(), "Sport", _assetVocabularyId,
			serviceContext);

		_sportAssetCategoryId = sportCategory.getCategoryId();

		AssetCategory travelCategory =
			AssetCategoryLocalServiceUtil.addCategory(
				TestPropsValues.getUserId(), "Travel", _assetVocabularyId,
				serviceContext);

		_travelAssetCategoryId = travelCategory.getCategoryId();

		_assetCategoryIds1 =
			new long[] {
				_healthAssetCategoryId, _sportAssetCategoryId,
				_travelAssetCategoryId
			};
		_assetCategoryIds2 = new long[] {
			_fashionAssetCategoryId, _foodAssetCategoryId,
			_healthAssetCategoryId, _sportAssetCategoryId
		};
	}

	@Test
	public void testAdvancedSearchAndOperator() throws Exception {
		testAdvancedSearch(
			"Test", "Cabina14", "Cabina14", true, "Cabina14 in Madrid",
			"Cabina14 blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchAndOperatorDescription1() throws Exception {
		testAdvancedSearch(
			"Julio", "Cabina14", "Cabina14", true, "Concert in Madrid",
			"Cabina14 blah blah blah", 0);
	}

	@Test
	public void testAdvancedSearchAndOperatorDescription2() throws Exception {
		testAdvancedSearch(
			"Test", null, "Cabina14", true, "Concert in Madrid",
			"Cabina14 blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchAndOperatorDescription3() throws Exception {
		testAdvancedSearch(
			null, null, "Cabina14", true, "Concert in Madrid",
			"Cabina14 blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchAndOperatorTitle1() throws Exception {
		testAdvancedSearch(
			"Julio", "Cabina14", "Cabina14", true, "Cabina14 in Madrid",
			"Cabina14 blah blah blah", 0);
	}

	@Test
	public void testAdvancedSearchAndOperatorTitle2() throws Exception {
		testAdvancedSearch(
			"Test", "Cabina14", null, true, "Cabina14 in Madrid",
			"blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchAndOperatorTitle3() throws Exception {
		testAdvancedSearch(
			null, "Cabina14", null, true, "Cabina14 in Madrid",
			"blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchAndOperatorUserName1() throws Exception {
		testAdvancedSearch(
			"Julio", "Cabina14", "Cabina14", true, "Cabina14 in Madrid",
			"Cabina14 blah blah blah", 0);
	}

	@Test
	public void testAdvancedSearchAndOperatorUserName2() throws Exception {
		testAdvancedSearch(
			"Test", null, "Cabina14", true, "Concert in Madrid",
			"Cabina14 blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchAndOperatorUserName3() throws Exception {
		testAdvancedSearch(
			"Test", null, null, true, "Concert in Madrid", "blah blah blah", 2);
	}

	@Test
	public void testAdvancedSearchOrOperator1() throws Exception {
		testAdvancedSearch(
			"Julio", "Cabina14", "Cabina14", false, "Concert in Madrid",
			"blah blah blah", 0);
	}

	@Test
	public void testAdvancedSearchOrOperator2() throws Exception {
		testAdvancedSearch(
			"Julio", "Cabina14", "Cabina14", false, "Cabina14 in Madrid",
			"blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchOrOperator3() throws Exception {
		testAdvancedSearch(
			"Julio", "Cabina14", "Cabina14", false, "Concert in Madrid",
			"Cabina14 blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchOrOperator4() throws Exception {
		testAdvancedSearch(
			"Test", "Cabina14", "Cabina14", false, "Concert in Madrid",
			"blah blah blah", 2);
	}

	@Test
	public void testAdvancedSearchOrOperator5() throws Exception {
		testAdvancedSearch(
			null, "Cabina14", null, false, "Cabina14 in Madrid",
			"blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchOrOperator6() throws Exception {
		testAdvancedSearch(
			null, "Cabina14", null, false, "Concert in Madrid",
			"blah blah blah", 0);
	}

	@Test
	public void testAdvancedSearchOrOperator7() throws Exception {
		testAdvancedSearch(
			null, null, "Cabina14", false, "Concert in Madrid",
			"Cabina14 blah blah blah", 1);
	}

	@Test
	public void testAdvancedSearchOrOperator8() throws Exception {
		testAdvancedSearch(
			null, null, "Cabina14", false, "Concert in Madrid",
			"blah blah blah", 0);
	}

	@Test
	public void testAdvancedSearchOrOperator9() throws Exception {
		testAdvancedSearch(
			"Test", null, null, false, "Concert in Madrid", "blah blah blah",
			2);
	}

	@Test
	public void testAdvancedSearchOrOperator10() throws Exception {
		testAdvancedSearch(
			"Julio", null, null, false, "Concert in Madrid", "blah blah blah",
			0);
	}

	@Test
	public void testAllAssetCategories1() throws Exception {
		testAssetCategories(
			new long[] {_healthAssetCategoryId}, false, false, 2);
	}

	@Test
	public void testAllAssetCategories2() throws Exception {
		testAssetCategories(
			new long[] {_healthAssetCategoryId, _sportAssetCategoryId},
			false, false, 2);
	}

	@Test
	public void testAllAssetCategories3() throws Exception {
		testAssetCategories(
			new long[] {
				_healthAssetCategoryId, _sportAssetCategoryId,
				_foodAssetCategoryId
			},
			false, false, 1);
	}

	@Test
	public void testAllAssetCategories4() throws Exception {
		testAssetCategories(
			new long[] {
				_healthAssetCategoryId, _sportAssetCategoryId,
				_foodAssetCategoryId, _travelAssetCategoryId
			},
			false, false, 0);
	}

	@Test
	public void testAllAssetTags1() throws Exception {
		testAssetTags(new String[] {"liferay"}, false, false, 2);
	}

	@Test
	public void testAllAssetTags2() throws Exception {
		testAssetTags(
			new String[] {"liferay", "architecture"}, false, false, 2);
	}

	@Test
	public void testAllAssetTags3() throws Exception {
		testAssetTags(
			new String[] {"liferay", "architecture", "services"}, false, false,
			1);
	}

	@Test
	public void testAllAssetTags4() throws Exception {
		testAssetTags(
			new String[] {"liferay", "architecture", "services", "osgi"}, false,
			false, 0);
	}

	@Test
	public void testAnyAssetCategories1() throws Exception {
		testAssetCategories(
			new long[] {_healthAssetCategoryId}, true, false, 2);
	}

	@Test
	public void testAnyAssetCategories2() throws Exception {
		testAssetCategories(
			new long[] {_healthAssetCategoryId, _sportAssetCategoryId},
			true, false, 2);
	}

	@Test
	public void testAnyAssetCategories3() throws Exception {
		testAssetCategories(
			new long[] {
				_healthAssetCategoryId, _sportAssetCategoryId,
				_foodAssetCategoryId
			},
			true, false, 2);
	}

	@Test
	public void testAnyAssetCategories4() throws Exception {
		testAssetCategories(
			new long[] {_fashionAssetCategoryId, _foodAssetCategoryId},
			true, false, 1);
	}

	@Test
	public void testAnyAssetTags1() throws Exception {
		testAssetTags(new String[] {"liferay"}, true, false, 2);
	}

	@Test
	public void testAnyAssetTags2() throws Exception {
		testAssetTags(new String[] {"liferay", "architecture"}, true, false, 2);
	}

	@Test
	public void testAnyAssetTags3() throws Exception {
		testAssetTags(
			new String[] {"liferay", "architecture", "services"}, true, false,
			2);
	}

	@Test
	public void testAnyAssetTags4() throws Exception {
		testAssetTags(new String[] {"modularity", "osgi"}, true, false, 1);
	}

	@Test
	public void testKeywordsDescription() throws Exception {
		testAssetKeywords(
			"Cabina14", "Concert in Madrid", "Cabina14 Blah blah blah", 1);
	}

	@Test
	public void testKeywordsNotFound() throws Exception {
		testAssetKeywords("Cabina14", "Concert in Madrid", "Blah blah blah", 0);
	}

	@Test
	public void testKeywordsTitle() throws Exception {
		testAssetKeywords(
			"Cabina14", "Cabina14 in Madrid", "Blah blah blah", 1);
	}

	@Test
	public void testKeywordsUserName() throws Exception {
		testAssetKeywords("Test", "Concert in Madrid", "Blah blah blah", 2);
	}

	@Test
	public void testNotAllAssetCategories1() throws Exception {
		testAssetCategories(
			new long[] {_healthAssetCategoryId}, false, true, 0);
	}

	@Test
	public void testNotAllAssetCategories2() throws Exception {
		testAssetCategories(
			new long[] {_healthAssetCategoryId, _sportAssetCategoryId},
			false, true, 0);
	}

	@Test
	public void testNotAllAssetCategories3() throws Exception {
		testAssetCategories(
			new long[] {_fashionAssetCategoryId, _foodAssetCategoryId},
			false, true, 1);
	}

	@Test
	public void testNotAllAssetCategories4() throws Exception {
		testAssetCategories(
			new long[] {
				_fashionAssetCategoryId, _foodAssetCategoryId,
				_travelAssetCategoryId
			},
			false, true, 2);
	}

	@Test
	public void testNotAllAssetTags1() throws Exception {
		testAssetTags(new String[] {"liferay"}, false, true, 0);
	}

	@Test
	public void testNotAllAssetTags2() throws Exception {
		testAssetTags(new String[] {"liferay", "architecture"}, false, true, 0);
	}

	@Test
	public void testNotAllAssetTags3() throws Exception {
		testAssetTags(
			new String[] {"liferay", "architecture", "services"}, false, true,
			1);
	}

	@Test
	public void testNotAllAssetTags4() throws Exception {
		testAssetTags(
			new String[] {"liferay", "architecture", "services", "osgi"}, false,
			true, 2);
	}

	@Test
	public void testNotAnyAssetCategories1() throws Exception {
		testAssetCategories(new long[] {_healthAssetCategoryId}, true, true, 0);
	}

	@Test
	public void testNotAnyAssetCategories2() throws Exception {
		testAssetCategories(
			new long[] {_healthAssetCategoryId, _sportAssetCategoryId},
			true, true, 0);
	}

	@Test
	public void testNotAnyAssetCategories3() throws Exception {
		testAssetCategories(
			new long[] {
				_fashionAssetCategoryId, _foodAssetCategoryId,
				_travelAssetCategoryId
			},
			true, true, 0);
	}

	@Test
	public void testNotAnyAssetCategories4() throws Exception {
		testAssetCategories(
			new long[] {_fashionAssetCategoryId, _foodAssetCategoryId},
			true, true, 1);
	}

	@Test
	public void testNotAnyAssetTags1() throws Exception {
		testAssetTags(new String[] {"liferay"}, true, true, 0);
	}

	@Test
	public void testNotAnyAssetTags2() throws Exception {
		testAssetTags(new String[] {"liferay", "architecture"}, true, true, 0);
	}

	@Test
	public void testNotAnyAssetTags3() throws Exception {
		testAssetTags(
			new String[] {"liferay", "architecture", "services"}, true, true,
			0);
	}

	@Test
	public void testNotAnyAssetTags4() throws Exception {
		testAssetTags(new String[] {"modularity", "osgi"}, true, true, 1);
	}

	@Test
	public void testOrderByRatingsAsc() throws Exception {
		double[] scores = {0.44, 0.2, 0.6, 0.22, 0.86};
		double[] orderedScores = {0.2, 0.22, 0.44, 0.6, 0.86};

		testOrderByRatings(scores, orderedScores, "ASC");
	}

	@Test
	public void testOrderByRatingsDesc() throws Exception {
		double[] scores = {0.44, 0.2, 0.6, 0.22, 0.86};
		double[] orderedScores = {0.86, 0.6, 0.44, 0.22, 0.2};

		testOrderByRatings(scores, orderedScores, "DESC");
	}

	@Test
	public void testOrderByViewCountsAsc() throws Exception {
		int[] viewCounts = new int[10];
		int[] orderedViewCounts = new int[10];

		for (int i = 0; i < viewCounts.length; i++) {
			int randomInt = RandomTestUtil.randomInt();

			viewCounts[i] = randomInt;
			orderedViewCounts[i] = randomInt;
		}

		Arrays.sort(orderedViewCounts);

		testOrderByViewCount(viewCounts, orderedViewCounts, "ASC");
	}

	@Test
	public void testOrderByViewCountsDesc() throws Exception {
		int[] viewCounts = new int[10];
		int[] orderedViewCounts = new int[10];

		for (int i = 0; i < viewCounts.length; i++) {
			int randomInt = RandomTestUtil.randomInt();

			viewCounts[i] = randomInt;
			orderedViewCounts[i] = randomInt;
		}

		Arrays.sort(orderedViewCounts);

		ArrayUtil.reverse(orderedViewCounts);

		testOrderByViewCount(viewCounts, orderedViewCounts, "DESC");
	}

	protected AssetEntryQuery buildAssetEntryQuery(
			long groupId, long[] assetCategoryIds, String[] assetTagNames,
			boolean any, boolean not)
		throws PortalException {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		if (assetCategoryIds != null) {
			assetEntryQuery = buildAssetEntryQueryWithAssetCategoryIds(
				assetEntryQuery, assetCategoryIds, any, not);
		}

		if (assetTagNames != null) {
			long[] assetTagIds = null;

			assetTagIds = AssetTagLocalServiceUtil.getTagIds(
				groupId, assetTagNames);

			assetEntryQuery = buildAssetEntryQueryWithAssetTagIds(
				assetEntryQuery, assetTagIds, any, not);
		}

		assetEntryQuery.setGroupIds(new long[] {groupId});

		return assetEntryQuery;
	}

	protected AssetEntryQuery buildAssetEntryQueryWithAdvancedSearch(
		long groupId, boolean andOperator, String title, String description,
		String userName) {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAndOperator(andOperator);
		assetEntryQuery.setDescription(description);
		assetEntryQuery.setGroupIds(new long[] {groupId});
		assetEntryQuery.setTitle(title);
		assetEntryQuery.setUserName(userName);

		return assetEntryQuery;
	}

	protected AssetEntryQuery buildAssetEntryQueryWithAssetCategoryIds(
		AssetEntryQuery assetEntryQuery, long[] assetCategoryIds, boolean any,
		boolean not) {

		if (any && not) {
			assetEntryQuery.setNotAnyCategoryIds(assetCategoryIds);
		}
		else if (!any && not) {
			assetEntryQuery.setNotAllCategoryIds(assetCategoryIds);
		}
		else if (any && !not) {
			assetEntryQuery.setAnyCategoryIds(assetCategoryIds);
		}
		else {
			assetEntryQuery.setAllCategoryIds(assetCategoryIds);
		}

		return assetEntryQuery;
	}

	protected AssetEntryQuery buildAssetEntryQueryWithAssetTagIds(
		AssetEntryQuery assetEntryQuery, long[] assetTagIds, boolean any,
		boolean not) {

		if (any && not) {
			assetEntryQuery.setNotAnyTagIds(assetTagIds);
		}
		else if (!any && not) {
			assetEntryQuery.setNotAllTagIds(assetTagIds);
		}
		else if (any && !not) {
			assetEntryQuery.setAnyTagIds(assetTagIds);
		}
		else {
			assetEntryQuery.setAllTagIds(assetTagIds);
		}

		return assetEntryQuery;
	}

	protected AssetEntryQuery buildAssetEntryQueryWithKeywords(
		long groupId, String keywords) {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setGroupIds(new long[] {groupId});
		assetEntryQuery.setKeywords(keywords);

		return assetEntryQuery;
	}

	protected void testAdvancedSearch(
			String searchUserName, String searchTitle, String searchDescription,
			boolean andOperator, String title, String description,
			int expectedAssetEntriesCount)
		throws Exception {

		ThreadLocalCache<Object[]> threadLocalCache =
			ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST, AssetEntryServiceImpl.class.getName());

		threadLocalCache.removeAll();

		AssetEntryQuery assetEntryQuery =
			buildAssetEntryQueryWithAdvancedSearch(
				_group.getGroupId(), andOperator, searchTitle,
				searchDescription, searchUserName);

		int initialAssetEntriesCount = AssetEntryServiceUtil.getEntriesCount(
			assetEntryQuery);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		BlogsEntry blogsEntry1 = BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringPool.BLANK, StringUtil.randomString(),
			RandomTestUtil.randomString(), 1, 1, 1965, 0, 0, true, true, null,
			null, null, serviceContext);

		BlogsEntry blogsEntry2 = BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), title, StringPool.BLANK, description,
			RandomTestUtil.randomString(), 1, 1, 1965, 0, 0, true, true, null,
			null, null, serviceContext);

		threadLocalCache.removeAll();

		int assetEntriesCount = AssetEntryServiceUtil.getEntriesCount(
			assetEntryQuery);

		Assert.assertEquals(
			initialAssetEntriesCount + expectedAssetEntriesCount,
			assetEntriesCount);

		BlogsEntryLocalServiceUtil.deleteEntry(blogsEntry1);
		BlogsEntryLocalServiceUtil.deleteEntry(blogsEntry2);
	}

	protected void testAssetCategories(
			long[] assetCategoryIds, boolean any, boolean not,
			int expectedAssetEntriesCount)
		throws Exception {

		testAssetCategorization(
			assetCategoryIds, null, "Skiing in the Alps", _assetCategoryIds1,
			null, "Keep your body in a good shape!", _assetCategoryIds2, null,
			any, not, expectedAssetEntriesCount);
	}

	protected void testAssetCategorization(
			long[] assetCategoryIds, String[] assetTagNames, String title1,
			long[] assetCategoryIds1, String[] assetTagNames1, String title2,
			long[] assetCategoryIds2, String[] assetTagNames2, boolean any,
			boolean not, int expectedAssetEntriesCount)
		throws Exception {

		ThreadLocalCache<Object[]> threadLocalCache =
			ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST, AssetEntryServiceImpl.class.getName());

		threadLocalCache.removeAll();

		AssetEntryQuery assetEntryQuery = buildAssetEntryQuery(
			_group.getGroupId(), assetCategoryIds, assetTagNames, any, not);

		int initialAssetEntriesCount = AssetEntryServiceUtil.getEntriesCount(
			assetEntryQuery);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		if (assetCategoryIds1 != null) {
			serviceContext.setAssetCategoryIds(assetCategoryIds1);
		}

		if (assetTagNames1 != null) {
			serviceContext.setAssetTagNames(assetTagNames1);
		}

		BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), title1, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomString(), 1, 1, 1965, 0, 0,
			true, true, null, null, null, serviceContext);

		if (assetCategoryIds2 != null) {
			serviceContext.setAssetCategoryIds(assetCategoryIds2);
		}

		if (assetTagNames2 != null) {
			serviceContext.setAssetTagNames(assetTagNames2);
		}

		BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), title2, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomString(), 1, 1, 1965, 0, 0,
			true, true, null, null, null, serviceContext);

		threadLocalCache.removeAll();

		assetEntryQuery = buildAssetEntryQuery(
			_group.getGroupId(), assetCategoryIds, assetTagNames, any, not);

		int assetEntriesCount = AssetEntryServiceUtil.getEntriesCount(
			assetEntryQuery);

		Assert.assertEquals(
			initialAssetEntriesCount + expectedAssetEntriesCount,
			assetEntriesCount);
	}

	protected void testAssetKeywords(
			String keywords, String title, String description,
			int expectedAssetEntriesCount)
		throws Exception {

		ThreadLocalCache<Object[]> threadLocalCache =
			ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST, AssetEntryServiceImpl.class.getName());

		threadLocalCache.removeAll();

		AssetEntryQuery assetEntryQuery = buildAssetEntryQueryWithKeywords(
			_group.getGroupId(), keywords);

		int initialAssetEntriesCount = AssetEntryServiceUtil.getEntriesCount(
			assetEntryQuery);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringPool.BLANK, StringUtil.randomString(),
			RandomTestUtil.randomString(), 1, 1, 1965, 0, 0, true, true, null,
			null, null, serviceContext);

		BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), title, StringPool.BLANK, description,
			RandomTestUtil.randomString(), 1, 1, 1965, 0, 0, true, true, null,
			null, null, serviceContext);

		threadLocalCache.removeAll();

		int assetEntriesCount = AssetEntryServiceUtil.getEntriesCount(
			assetEntryQuery);

		Assert.assertEquals(
			initialAssetEntriesCount + expectedAssetEntriesCount,
			assetEntriesCount);
	}

	protected void testAssetTags(
			String[] assetTagNames, boolean any, boolean not,
			int expectedAssetEntriesCount)
		throws Exception {

		testAssetCategorization(
			null, assetTagNames, "Liferay Architectural Approach", null,
			new String[] {"liferay", "architecture", "services"},
			"Modularity with OSGI", null,
			new String[] {"liferay", "architecture", "modularity", "osgi"}, any,
			not, expectedAssetEntriesCount);
	}

	protected void testOrderByRatings(
			double[] scores, double[] orderedScores, String orderByType)
		throws Exception {

		ThreadLocalCache<Object[]> threadLocalCache =
			ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST, AssetEntryServiceImpl.class.getName());

		threadLocalCache.removeAll();

		for (int i = 0; i < scores.length; i++) {
			BlogsEntry entry = BlogsTestUtil.addEntry(_group, true);

			RatingsEntryServiceUtil.updateEntry(
				BlogsEntry.class.getName(), entry.getEntryId(), scores[i]);
		}

		threadLocalCache.removeAll();

		AssetEntryQuery assetEntryQuery = buildAssetEntryQuery(
			_group.getGroupId(), null, null, false, false);

		assetEntryQuery.setOrderByCol1("ratings");
		assetEntryQuery.setOrderByType1(orderByType);

		List<AssetEntry> assetEntries = AssetEntryServiceUtil.getEntries(
			assetEntryQuery);

		for (int i = 0; i < assetEntries.size(); i++) {
			AssetEntry assetEntry = assetEntries.get(i);

			RatingsStats ratingsStats = RatingsStatsLocalServiceUtil.getStats(
				assetEntry.getClassName(), assetEntry.getClassPK());

			Assert.assertEquals(
				ratingsStats.getAverageScore(), orderedScores[i], 0);
		}
	}

	protected void testOrderByViewCount(
			int[] viewCounts, int[] orderedViewCounts, String orderByType)
		throws Exception {

		ThreadLocalCache<Object[]> threadLocalCache =
			ThreadLocalCacheManager.getThreadLocalCache(
				Lifecycle.REQUEST, AssetEntryServiceImpl.class.getName());

		threadLocalCache.removeAll();

		for (int i = 0; i < viewCounts.length; i++) {
			BlogsEntry entry = BlogsTestUtil.addEntry(_group, true);

			AssetEntry assetEntry = AssetEntryLocalServiceUtil.getEntry(
				BlogsEntry.class.getName(), entry.getEntryId());

			assetEntry.setViewCount(viewCounts[i]);

			AssetEntryLocalServiceUtil.updateAssetEntry(assetEntry);
		}

		threadLocalCache.removeAll();

		AssetEntryQuery assetEntryQuery = buildAssetEntryQuery(
			_group.getGroupId(), null, null, false, false);

		assetEntryQuery.setOrderByCol1("viewCount");
		assetEntryQuery.setOrderByType1(orderByType);

		List<AssetEntry> assetEntries = AssetEntryServiceUtil.getEntries(
			assetEntryQuery);

		for (int i = 0; i < assetEntries.size(); i++) {
			AssetEntry assetEntry = assetEntries.get(i);

			Assert.assertEquals(
				assetEntry.getViewCount(), orderedViewCounts[i]);
		}
	}

	private long[] _assetCategoryIds1;
	private long[] _assetCategoryIds2;
	private long _assetVocabularyId;
	private long _fashionAssetCategoryId;
	private long _foodAssetCategoryId;

	@DeleteAfterTestRun
	private Group _group;

	private long _healthAssetCategoryId;
	private long _sportAssetCategoryId;
	private long _travelAssetCategoryId;

}