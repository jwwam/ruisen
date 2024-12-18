import request from '/@/utils/request';

/**
 * 根据分页查询参数获取列表数据。
 * @param {Object} [query] - 查询参数。
 * @returns {Promise} 请求的 Promise 分页对象。
 */
export function fetchList(query?: Object) {
	return request({
		url: '/rs/customerGamEmails/page',
		method: 'get',
		params: query,
	});
}

/**
 * 根据分页查询参数获取列表数据。
 * @param {Object} [query] - 查询参数。
 * @returns {Promise} 请求的 Promise 分页对象。
 */
export function fetchNewList(query?: Object) {
	return request({
		url: '/rs/customerGamEmails/newPage',
		method: 'get',
		params: query,
	});
}
/**
 * 根据分页查询参数获取列表数据。
 * @param {Object} [query] - 查询参数。
 * @returns {Promise} 请求的 Promise 分页对象。
 */
export function fetchListWithoutRole(query?: Object) {
	return request({
		url: '/rs/customerGamEmails/fetchListWithoutRole',
		method: 'get',
		params: query,
	});
}
/**
 * 添加一个新对象。
 * @param {Object} [obj] - 要添加的对象。
 * @returns {Promise} 请求的 Promise 对象 （true/false）。
 */
export function addObj(obj?: Object) {
	return request({
		url: '/rs/customerGamEmails',
		method: 'post',
		data: obj,
	});
}

/**
 * 根据查询参数获取对象详情。
 * @param {Object} [obj] - 查询参数。
 * @returns {Promise} 请求的 Promise 对象数组。
 */
export function getObj(obj?: Object) {
	return request({
		url: '/rs/customerGamEmails/details',
		method: 'get',
		params: obj,
	});
}

/**
 * 根据 ID 删除对象。
 * @param {Object} [ids] - 要删除的对象 ID。
 * @returns {Promise} 请求的 Promise 对象。
 */
export function delObjs(ids?: Object) {
	return request({
		url: '/rs/customerGamEmails',
		method: 'delete',
		data: ids,
	});
}

/**
 * 更新一个已存在的对象。
 * @param {Object} [obj] - 要更新的对象。
 * @returns {Promise} 请求的 Promise 对象。
 */
export function putObj(obj?: Object) {
	return request({
		url: '/rs/customerGamEmails',
		method: 'put',
		data: obj,
	});
}

/**
 * 验证某个字段的值是否已经存在。
 * @param {Object} rule - 验证规则对象。
 * @param {*} value - 要验证的值。
 * @param {Function} callback - 验证完成后的回调函数。
 * @param {boolean} isEdit - 当前操作是否为编辑。
 *
 * 示例用法：
 * 字段名: [
 *   {
 *     validator: (rule, value, callback) => {
 *       validateExist(rule, value, callback, form.emailId !== '');
 *     },
 *     trigger: 'blur',
 *   },
 * ]
 */
export function validateExist(rule: any, value: any, callback: any, isEdit: boolean) {
	if (isEdit) {
		return callback();
	}

	getObj({ [rule.field]: value }).then((response) => {
		const result = response.data;
		if (result !== null && result.length > 0) {
			callback(new Error('数据已经存在'));
		} else {
			callback();
		}
	});
}
