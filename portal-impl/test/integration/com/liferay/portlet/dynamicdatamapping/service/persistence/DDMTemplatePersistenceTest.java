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

package com.liferay.portlet.dynamicdatamapping.service.persistence;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.AggregateTestRule;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.LiferayIntegrationTestRule;
import com.liferay.portal.test.PersistenceTestRule;
import com.liferay.portal.test.TransactionalTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.test.RandomTestUtil;

import com.liferay.portlet.dynamicdatamapping.NoSuchTemplateException;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.model.impl.DDMTemplateModelImpl;
import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
public class DDMTemplatePersistenceTest {
	@Rule
	public final AggregateTestRule aggregateTestRule = new AggregateTestRule(new LiferayIntegrationTestRule(),
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@After
	public void tearDown() throws Exception {
		Iterator<DDMTemplate> iterator = _ddmTemplates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate ddmTemplate = _persistence.create(pk);

		Assert.assertNotNull(ddmTemplate);

		Assert.assertEquals(ddmTemplate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		_persistence.remove(newDDMTemplate);

		DDMTemplate existingDDMTemplate = _persistence.fetchByPrimaryKey(newDDMTemplate.getPrimaryKey());

		Assert.assertNull(existingDDMTemplate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMTemplate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate newDDMTemplate = _persistence.create(pk);

		newDDMTemplate.setUuid(RandomTestUtil.randomString());

		newDDMTemplate.setGroupId(RandomTestUtil.nextLong());

		newDDMTemplate.setCompanyId(RandomTestUtil.nextLong());

		newDDMTemplate.setUserId(RandomTestUtil.nextLong());

		newDDMTemplate.setUserName(RandomTestUtil.randomString());

		newDDMTemplate.setCreateDate(RandomTestUtil.nextDate());

		newDDMTemplate.setModifiedDate(RandomTestUtil.nextDate());

		newDDMTemplate.setClassNameId(RandomTestUtil.nextLong());

		newDDMTemplate.setClassPK(RandomTestUtil.nextLong());

		newDDMTemplate.setTemplateKey(RandomTestUtil.randomString());

		newDDMTemplate.setName(RandomTestUtil.randomString());

		newDDMTemplate.setDescription(RandomTestUtil.randomString());

		newDDMTemplate.setType(RandomTestUtil.randomString());

		newDDMTemplate.setMode(RandomTestUtil.randomString());

		newDDMTemplate.setLanguage(RandomTestUtil.randomString());

		newDDMTemplate.setScript(RandomTestUtil.randomString());

		newDDMTemplate.setCacheable(RandomTestUtil.randomBoolean());

		newDDMTemplate.setSmallImage(RandomTestUtil.randomBoolean());

		newDDMTemplate.setSmallImageId(RandomTestUtil.nextLong());

		newDDMTemplate.setSmallImageURL(RandomTestUtil.randomString());

		_ddmTemplates.add(_persistence.update(newDDMTemplate));

		DDMTemplate existingDDMTemplate = _persistence.findByPrimaryKey(newDDMTemplate.getPrimaryKey());

		Assert.assertEquals(existingDDMTemplate.getUuid(),
			newDDMTemplate.getUuid());
		Assert.assertEquals(existingDDMTemplate.getTemplateId(),
			newDDMTemplate.getTemplateId());
		Assert.assertEquals(existingDDMTemplate.getGroupId(),
			newDDMTemplate.getGroupId());
		Assert.assertEquals(existingDDMTemplate.getCompanyId(),
			newDDMTemplate.getCompanyId());
		Assert.assertEquals(existingDDMTemplate.getUserId(),
			newDDMTemplate.getUserId());
		Assert.assertEquals(existingDDMTemplate.getUserName(),
			newDDMTemplate.getUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingDDMTemplate.getCreateDate()),
			Time.getShortTimestamp(newDDMTemplate.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingDDMTemplate.getModifiedDate()),
			Time.getShortTimestamp(newDDMTemplate.getModifiedDate()));
		Assert.assertEquals(existingDDMTemplate.getClassNameId(),
			newDDMTemplate.getClassNameId());
		Assert.assertEquals(existingDDMTemplate.getClassPK(),
			newDDMTemplate.getClassPK());
		Assert.assertEquals(existingDDMTemplate.getTemplateKey(),
			newDDMTemplate.getTemplateKey());
		Assert.assertEquals(existingDDMTemplate.getName(),
			newDDMTemplate.getName());
		Assert.assertEquals(existingDDMTemplate.getDescription(),
			newDDMTemplate.getDescription());
		Assert.assertEquals(existingDDMTemplate.getType(),
			newDDMTemplate.getType());
		Assert.assertEquals(existingDDMTemplate.getMode(),
			newDDMTemplate.getMode());
		Assert.assertEquals(existingDDMTemplate.getLanguage(),
			newDDMTemplate.getLanguage());
		Assert.assertEquals(existingDDMTemplate.getScript(),
			newDDMTemplate.getScript());
		Assert.assertEquals(existingDDMTemplate.getCacheable(),
			newDDMTemplate.getCacheable());
		Assert.assertEquals(existingDDMTemplate.getSmallImage(),
			newDDMTemplate.getSmallImage());
		Assert.assertEquals(existingDDMTemplate.getSmallImageId(),
			newDDMTemplate.getSmallImageId());
		Assert.assertEquals(existingDDMTemplate.getSmallImageURL(),
			newDDMTemplate.getSmallImageURL());
	}

	@Test
	public void testCountByUuid() {
		try {
			_persistence.countByUuid(StringPool.BLANK);

			_persistence.countByUuid(StringPool.NULL);

			_persistence.countByUuid((String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByUUID_G() {
		try {
			_persistence.countByUUID_G(StringPool.BLANK,
				RandomTestUtil.nextLong());

			_persistence.countByUUID_G(StringPool.NULL, 0L);

			_persistence.countByUUID_G((String)null, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByUuid_C() {
		try {
			_persistence.countByUuid_C(StringPool.BLANK,
				RandomTestUtil.nextLong());

			_persistence.countByUuid_C(StringPool.NULL, 0L);

			_persistence.countByUuid_C((String)null, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByGroupId() {
		try {
			_persistence.countByGroupId(RandomTestUtil.nextLong());

			_persistence.countByGroupId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByClassPK() {
		try {
			_persistence.countByClassPK(RandomTestUtil.nextLong());

			_persistence.countByClassPK(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByTemplateKey() {
		try {
			_persistence.countByTemplateKey(StringPool.BLANK);

			_persistence.countByTemplateKey(StringPool.NULL);

			_persistence.countByTemplateKey((String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByType() {
		try {
			_persistence.countByType(StringPool.BLANK);

			_persistence.countByType(StringPool.NULL);

			_persistence.countByType((String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByLanguage() {
		try {
			_persistence.countByLanguage(StringPool.BLANK);

			_persistence.countByLanguage(StringPool.NULL);

			_persistence.countByLanguage((String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountBySmallImageId() {
		try {
			_persistence.countBySmallImageId(RandomTestUtil.nextLong());

			_persistence.countBySmallImageId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C() {
		try {
			_persistence.countByG_C(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong());

			_persistence.countByG_C(0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_CPK() {
		try {
			_persistence.countByG_CPK(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong());

			_persistence.countByG_CPK(0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_CPKArrayable() {
		try {
			_persistence.countByG_CPK(new long[] { RandomTestUtil.nextLong(), 0L },
				RandomTestUtil.nextLong());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_C() {
		try {
			_persistence.countByG_C_C(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

			_persistence.countByG_C_C(0L, 0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_CArrayable() {
		try {
			_persistence.countByG_C_C(new long[] { RandomTestUtil.nextLong(), 0L },
				RandomTestUtil.nextLong(), RandomTestUtil.nextLong());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_T() {
		try {
			_persistence.countByG_C_T(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), StringPool.BLANK);

			_persistence.countByG_C_T(0L, 0L, StringPool.NULL);

			_persistence.countByG_C_T(0L, 0L, (String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByC_C_T() {
		try {
			_persistence.countByC_C_T(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), StringPool.BLANK);

			_persistence.countByC_C_T(0L, 0L, StringPool.NULL);

			_persistence.countByC_C_T(0L, 0L, (String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_C_T() {
		try {
			_persistence.countByG_C_C_T(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
				StringPool.BLANK);

			_persistence.countByG_C_C_T(0L, 0L, 0L, StringPool.NULL);

			_persistence.countByG_C_C_T(0L, 0L, 0L, (String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_C_C_T_M() {
		try {
			_persistence.countByG_C_C_T_M(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
				StringPool.BLANK, StringPool.BLANK);

			_persistence.countByG_C_C_T_M(0L, 0L, 0L, StringPool.NULL,
				StringPool.NULL);

			_persistence.countByG_C_C_T_M(0L, 0L, 0L, (String)null, (String)null);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		DDMTemplate existingDDMTemplate = _persistence.findByPrimaryKey(newDDMTemplate.getPrimaryKey());

		Assert.assertEquals(existingDDMTemplate, newDDMTemplate);
	}

	@Test
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			Assert.fail("Missing entity did not throw NoSuchTemplateException");
		}
		catch (NoSuchTemplateException nsee) {
		}
	}

	@Test
	public void testFindAll() throws Exception {
		try {
			_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		try {
			_persistence.filterFindByGroupId(0, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected OrderByComparator<DDMTemplate> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("DDMTemplate", "uuid", true,
			"templateId", true, "groupId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "templateKey", true, "name",
			true, "description", true, "type", true, "mode", true, "language",
			true, "script", true, "cacheable", true, "smallImage", true,
			"smallImageId", true, "smallImageURL", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		DDMTemplate existingDDMTemplate = _persistence.fetchByPrimaryKey(newDDMTemplate.getPrimaryKey());

		Assert.assertEquals(existingDDMTemplate, newDDMTemplate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate missingDDMTemplate = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMTemplate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		DDMTemplate newDDMTemplate1 = addDDMTemplate();
		DDMTemplate newDDMTemplate2 = addDDMTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplate1.getPrimaryKey());
		primaryKeys.add(newDDMTemplate2.getPrimaryKey());

		Map<Serializable, DDMTemplate> ddmTemplates = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmTemplates.size());
		Assert.assertEquals(newDDMTemplate1,
			ddmTemplates.get(newDDMTemplate1.getPrimaryKey()));
		Assert.assertEquals(newDDMTemplate2,
			ddmTemplates.get(newDDMTemplate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMTemplate> ddmTemplates = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMTemplate> ddmTemplates = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTemplates.size());
		Assert.assertEquals(newDDMTemplate,
			ddmTemplates.get(newDDMTemplate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMTemplate> ddmTemplates = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTemplate.getPrimaryKey());

		Map<Serializable, DDMTemplate> ddmTemplates = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTemplates.size());
		Assert.assertEquals(newDDMTemplate,
			ddmTemplates.get(newDDMTemplate.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = DDMTemplateLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod() {
				@Override
				public void performAction(Object object) {
					DDMTemplate ddmTemplate = (DDMTemplate)object;

					Assert.assertNotNull(ddmTemplate);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DDMTemplate.class,
				DDMTemplate.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("templateId",
				newDDMTemplate.getTemplateId()));

		List<DDMTemplate> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		DDMTemplate existingDDMTemplate = result.get(0);

		Assert.assertEquals(existingDDMTemplate, newDDMTemplate);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DDMTemplate.class,
				DDMTemplate.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("templateId",
				RandomTestUtil.nextLong()));

		List<DDMTemplate> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		DDMTemplate newDDMTemplate = addDDMTemplate();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DDMTemplate.class,
				DDMTemplate.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("templateId"));

		Object newTemplateId = newDDMTemplate.getTemplateId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("templateId",
				new Object[] { newTemplateId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingTemplateId = result.get(0);

		Assert.assertEquals(existingTemplateId, newTemplateId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(DDMTemplate.class,
				DDMTemplate.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("templateId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("templateId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		if (!PropsValues.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			return;
		}

		DDMTemplate newDDMTemplate = addDDMTemplate();

		_persistence.clearCache();

		DDMTemplateModelImpl existingDDMTemplateModelImpl = (DDMTemplateModelImpl)_persistence.findByPrimaryKey(newDDMTemplate.getPrimaryKey());

		Assert.assertTrue(Validator.equals(
				existingDDMTemplateModelImpl.getUuid(),
				existingDDMTemplateModelImpl.getOriginalUuid()));
		Assert.assertEquals(existingDDMTemplateModelImpl.getGroupId(),
			existingDDMTemplateModelImpl.getOriginalGroupId());

		Assert.assertEquals(existingDDMTemplateModelImpl.getSmallImageId(),
			existingDDMTemplateModelImpl.getOriginalSmallImageId());

		Assert.assertEquals(existingDDMTemplateModelImpl.getGroupId(),
			existingDDMTemplateModelImpl.getOriginalGroupId());
		Assert.assertEquals(existingDDMTemplateModelImpl.getClassNameId(),
			existingDDMTemplateModelImpl.getOriginalClassNameId());
		Assert.assertTrue(Validator.equals(
				existingDDMTemplateModelImpl.getTemplateKey(),
				existingDDMTemplateModelImpl.getOriginalTemplateKey()));
	}

	protected DDMTemplate addDDMTemplate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTemplate ddmTemplate = _persistence.create(pk);

		ddmTemplate.setUuid(RandomTestUtil.randomString());

		ddmTemplate.setGroupId(RandomTestUtil.nextLong());

		ddmTemplate.setCompanyId(RandomTestUtil.nextLong());

		ddmTemplate.setUserId(RandomTestUtil.nextLong());

		ddmTemplate.setUserName(RandomTestUtil.randomString());

		ddmTemplate.setCreateDate(RandomTestUtil.nextDate());

		ddmTemplate.setModifiedDate(RandomTestUtil.nextDate());

		ddmTemplate.setClassNameId(RandomTestUtil.nextLong());

		ddmTemplate.setClassPK(RandomTestUtil.nextLong());

		ddmTemplate.setTemplateKey(RandomTestUtil.randomString());

		ddmTemplate.setName(RandomTestUtil.randomString());

		ddmTemplate.setDescription(RandomTestUtil.randomString());

		ddmTemplate.setType(RandomTestUtil.randomString());

		ddmTemplate.setMode(RandomTestUtil.randomString());

		ddmTemplate.setLanguage(RandomTestUtil.randomString());

		ddmTemplate.setScript(RandomTestUtil.randomString());

		ddmTemplate.setCacheable(RandomTestUtil.randomBoolean());

		ddmTemplate.setSmallImage(RandomTestUtil.randomBoolean());

		ddmTemplate.setSmallImageId(RandomTestUtil.nextLong());

		ddmTemplate.setSmallImageURL(RandomTestUtil.randomString());

		_ddmTemplates.add(_persistence.update(ddmTemplate));

		return ddmTemplate;
	}

	private List<DDMTemplate> _ddmTemplates = new ArrayList<DDMTemplate>();
	private DDMTemplatePersistence _persistence = DDMTemplateUtil.getPersistence();
}