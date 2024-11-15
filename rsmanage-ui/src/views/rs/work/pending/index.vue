<template>
	<div class="layout-padding">
	  <div class="layout-padding-auto layout-padding-view">
		<el-row v-show="showSearch">
			<el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
				<!-- <el-form-item label="提交人" prop="submitterId">
					<el-select v-model="state.queryForm.submitterId" placeholder="请选择提交人" filterable>
						<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
					</el-select>
				</el-form-item> -->
				<el-form-item label="工单分类" prop="category">
					<el-input v-model="state.queryForm.category" placeholder="请输入工单分类" clearable style="width: 180px" />
				</el-form-item>
				<el-form-item label="工单状态" prop="status">
					<el-select v-model="state.queryForm.status" placeholder="请选择工单状态" filterable>
						<el-option :label="item.label" :value="item.value" v-for="(item, index) in work_status" :key="index"></el-option>
					</el-select>
				</el-form-item>
				<!-- <el-form-item label="处理人" prop="assignees">
					<el-select v-model="state.queryForm.assignees" placeholder="请选择处理人" filterable>
						<el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId"></el-option>
					</el-select>
				</el-form-item> -->
				<el-form-item>
					<el-button icon="search" type="primary" @click="getDataList"> 查 询 </el-button>
					<el-button icon="Refresh" @click="resetQuery">重 置</el-button>
				</el-form-item>
			</el-form>
		</el-row>
		<!-- <el-row>
			<div class="mb8" style="width: 100%">
				<el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()" v-auth="'rs_work_add'"> 新 增 </el-button>
				<el-button plain icon="upload-filled" type="primary" class="ml10" @click="excelUploadRef.show()" v-auth="'sys_user_add'"> 导 入 </el-button>
				<el-button plain :disabled="multiple" icon="Delete" type="primary" v-auth="'rs_work_del'" @click="handleDelete(selectObjs)">
					删 除
				</el-button>
				<right-toolbar
					v-model:showSearch="showSearch"
					:export="'rs_work_export'"
					@exportExcel="exportExcel"
					class="ml10 mr20"
					style="float: right"
					@queryTable="getDataList"
				></right-toolbar>
			</div>
		</el-row> -->
  
		<el-scrollbar>
		  <div class="mx-auto mt-4">
			<!-- 待处理和处理中的工单区域 -->
			<div class="mb-8">
			  <div class="flex items-center justify-between mb-4">
				<div class="flex items-center">
				  <h2 class="text-xl font-bold text-gray-900">待办工单</h2>
				  <el-tag type="danger" class="ml-2 text-sm" round>
					{{ getPendingWorks.length }}
				  </el-tag>
				</div>
			  </div>
			  <div class="px-4">
				<div class="grid sm:grid-cols-2 sm:gap-x-6 lg:grid-cols-3">
				  <div class="mb-6 rounded-lg p-6 cursor-pointer hover:shadow-lg transition-shadow" 
					   :style="{ backgroundColor: getBackgroundColor(work.status) }" 
					   v-for="work in getPendingWorks"
					   :key="work.workId"
					   @click="view(work.workId)">
					<div class="flex items-center justify-between">
					  <div class="flex items-center">
						<h3 class="text-base font-semibold text-gray-900">{{ work.title }}</h3>
					  </div>
					  <div class="flex items-end">
						<svg t="1710908184286" viewBox="0 0 1024 1024" version="1.1"
							 xmlns="http://www.w3.org/2000/svg" p-id="5178" class="mr-2 h-5 w-5 text-base text-gray-500">
						  <path
							d="M512 1003.52a497.92 497.92 0 1 1 497.92-497.92A498.56 498.56 0 0 1 512 1003.52zM512 71.68a433.92 433.92 0 1 0 433.92 433.92A434.56 434.56 0 0 0 512 71.68z"
							fill="#323333" p-id="5179"></path>
						  <path
							d="M152.96 369.92a33.92 33.92 0 0 1 35.2-36.48 39.04 39.04 0 0 1 29.44 16l148.48 198.4V369.92a35.2 35.2 0 1 1 69.76 0V640a30.72 30.72 0 0 1-34.56 33.28 36.48 36.48 0 0 1-29.44-12.8l-147.2-198.4V640a30.72 30.72 0 0 1-34.56 33.28 31.36 31.36 0 0 1-37.12-33.28zM463.36 504.32a162.56 162.56 0 1 1 323.84 0 158.08 158.08 0 0 1-161.92 168.96 159.36 159.36 0 0 1-161.92-168.96z m252.16 0c-3.84-69.12-33.92-104.96-90.24-108.8s-84.48 39.68-88.32 108.8 33.28 104.32 88.32 108.16 86.4-36.48 90.24-108.16zM856.96 603.52A37.12 37.12 0 0 1 896 640c0 21.12-13.44 32-36.48 33.28s-35.84-12.16-37.12-33.28a37.76 37.76 0 0 1 34.56-36.48z"
							fill="#323333" p-id="5180"></path>
						</svg>
						<span class="mr-1">{{ work.workId }}</span>
					  </div>
					</div>
					<p class="my-3 text-sm font-normal text-gray-500 flex items-center">
					  <svg t="1730975355744" class="mr-1 h-5 w-6 text-yellow-500" viewBox="0 0 1024 1024" version="1.1"
						 xmlns="http://www.w3.org/2000/svg" p-id="4273" width="128" height="128">
						   <path d="M954.88 237.568c0-60.416-56.832-109.568-126.464-109.568H195.584c-70.144 0-126.464 49.152-126.464 109.568v548.352c0 60.416 56.832 109.568 126.464 109.568h632.832c70.144 0 126.464-49.152 126.464-109.568V237.568z m-803.84-28.16c11.776-10.24 28.16-16.384 45.056-15.872h632.832c34.816 0 63.488 14.336 63.488 44.544v246.784h-50.176l-54.272-47.104c-12.288-10.752-32.256-10.752-44.544 0-3.584 3.072-6.144 6.656-7.68 10.752l-38.912 101.888L604.16 390.144c-7.68-13.312-26.624-18.944-42.496-12.288-7.168 3.072-12.288 8.192-15.36 14.336l-92.16 199.68-101.376-307.2c-4.608-14.336-22.528-23.04-38.912-18.944-8.704 2.048-15.872 7.168-19.456 14.336l-117.76 204.288H133.12V247.808c-1.024-14.336 6.144-28.16 17.92-38.4z m725.504 611.84c-7.68 7.68-31.232 9.728-48.128 9.728H195.584c-16.896 0-32.768-5.632-45.056-15.872s-18.432-24.064-18.432-38.912v-236.544h63.488c11.776 0 23.04-5.632 28.16-15.36l90.112-156.16 104.448 316.416c3.584 11.264 15.36 19.456 28.672 19.968h1.536c12.8 0 24.576-6.656 29.184-17.408L578.56 468.992l95.232 164.864c7.68 13.312 26.624 18.944 42.496 12.288 7.68-3.072 13.312-8.704 15.872-15.872l47.104-122.368 27.136 23.552c6.144 5.12 13.824 8.192 22.528 8.192h62.976v236.544c0 14.336-9.728 40.448-14.336 44.544l-1.024 0.512z" 
							 p-id="4274">
							</path>
					</svg>
					<span class="mr-2">状态：</span>
					<el-tag
					  :type="getStatusType(work.status)"
					  class="ml-1"
					>
					  {{ work_status.find(item => item.value === String(work.status))?.label }}
					</el-tag>
				  </p>
				  <p class="my-3 text-sm font-normal text-gray-500 flex items-center">
					<svg t="1730967494215" class="mr-1 h-5 w-6 text-yellow-500" viewBox="0 0 1024 1024" version="1.1"
						   xmlns="http://www.w3.org/2000/svg" p-id="934" width="128" height="128">
						   <path d="M85.333333 227.555556A85.333333 85.333333 0 0 1 170.666667 142.222222h682.666666A85.333333 85.333333 0 0 1 938.666667 227.555556v170.666666a28.444444 28.444444 0 0 1-28.444445 28.444445H113.777778a28.444444 28.444444 0 0 1-28.444445-28.444445V227.555556zM170.666667 199.111111a28.444444 28.444444 0 0 0-28.444445 28.444445v142.222222h739.555556V227.555556a28.444444 28.444444 0 0 0-28.444445-28.444445H170.666667z" p-id="935"></path><path d="M256 85.333333A28.444444 28.444444 0 0 1 284.444444 113.777778v113.777778a28.444444 28.444444 0 0 1-56.888888 0V113.777778a28.444444 28.444444 0 0 1 28.444444-28.444445z m512 0a28.444444 28.444444 0 0 1 28.444444 28.444445v113.777778a28.444444 28.444444 0 0 1-56.888888 0V113.777778a28.444444 28.444444 0 0 1 28.444444-28.444445zM711.111111 540.444444a170.666667 170.666667 0 1 0 0 341.333334 170.666667 170.666667 0 0 0 0-341.333334z m-227.555555 170.666667a227.555556 227.555556 0 1 1 455.111111 0 227.555556 227.555556 0 0 1-455.111111 0z" p-id="936"></path><path d="M682.666667 597.333333a28.444444 28.444444 0 0 1 28.444444 28.444445v85.333333H796.444444a28.444444 28.444444 0 0 1 0 56.888889h-113.777777a28.444444 28.444444 0 0 1-28.444445-28.444444v-113.777778a28.444444 28.444444 0 0 1 28.444445-28.444445zM113.777778 369.777778a28.444444 28.444444 0 0 1 28.444444 28.444444v426.666667a28.444444 28.444444 0 0 0 28.444445 28.444444h426.666666a28.444444 28.444444 0 0 1 0 56.888889H170.666667a85.333333 85.333333 0 0 1-85.333334-85.333333V398.222222a28.444444 28.444444 0 0 1 28.444445-28.444444z" p-id="937"></path><path d="M199.111111 540.444444A28.444444 28.444444 0 0 1 227.555556 512h199.111111a28.444444 28.444444 0 0 1 0 56.888889H227.555556a28.444444 28.444444 0 0 1-28.444445-28.444445z m0 170.666667A28.444444 28.444444 0 0 1 227.555556 682.666667h85.333333a28.444444 28.444444 0 0 1 0 56.888889H227.555556a28.444444 28.444444 0 0 1-28.444445-28.444445z" p-id="938">
						</path>
					  </svg>
					  <span>有效期：{{ parseDate(work.createdAt) }} - {{ parseDate(work.deadline) }}</span>
				  </p>
				  <div class="mt-6 flex items-center justify-between text-sm font-semibold text-gray-900">
					<div class="flex items-center">
					  <svg t="1730968056941" 
						   class="mr-1 h-5 w-6 text-yellow-500" 
						   viewBox="0 0 1024 1024" 
						   version="1.1"
						   xmlns="http://www.w3.org/2000/svg" 
						   p-id="4270" 
						   width="128" 
						   height="128">
						<path d="M319.968 960c-5.92 0-11.904-1.664-17.184-4.992-12.672-8.064-18.016-23.84-12.896-37.952L402.304 608 160 608c-12.928 0-24.608-7.776-29.568-19.744s-2.208-25.728 6.944-34.88l480-480c12.512-12.512 32.736-12.512 45.248 0s12.512 32.736 0 45.248L237.248 544 448 544c10.432 0 20.224 5.088 26.208 13.664 6.016 8.544 7.424 19.456 3.872 29.28l-78.72 216.448L786.752 416 576 416c-17.696 0-32-14.336-32-32s14.304-32 32-32l288 0c12.928 0 24.64 7.808 29.568 19.744 4.96 11.968 2.208 25.728-6.944 34.88l-544 544C336.448 956.8 328.224 960 319.968 960z" 
							  fill="#d81e06" 
							  p-id="4271"/>
					  </svg>
					  <span class="mr-2">级别：</span>
					  <!-- type["primary", "success", "info", "warning", "danger"] -->
					  <el-tag
						:type="work.priority === '紧急' ? 'danger' : 'primary'" 
						class="ml-1"
						>
						{{ work.priority }}
					  </el-tag>
					</div>
					<div class="flex items-center">
  
					  <el-button class="!p-0" link icon="View"  @click="view(work.workId)" text
								 type="primary"
					  >查看
					  </el-button>
					  <!-- <el-button class="!p-0" icon="edit-pen" @click="formDialogRef.openDialog(work.workId)" text
								 type="primary"
								 v-auth="'rs_work_edit'"
					  >编辑
					  </el-button> -->
					  <!-- <el-button
						  class="!p-0"
						  icon="delete"
						  :disabled="work.workId === '1'"
						  @click="handleDelete([work.workId])"
						  text
						  type="primary"
						  v-auth="'rs_work_del'"
					  >删除
					  </el-button> -->
					</div>
				  </div>
				</div>
			  </div>
			</div>
			</div>
			</div>

			<!-- 已处理的工单区域 -->
			<div>
			  <div class="flex items-center justify-between mb-4">
				<div class="flex items-center">
				  <h2 class="text-xl font-bold text-gray-900">已办工单</h2>
				  <el-tag type="info" class="ml-2 text-sm" round>
					{{ getCompletedWorks.length }}
				  </el-tag>
				</div>
			  </div>
			  <div class="px-4">
				<div class="grid sm:grid-cols-2 sm:gap-x-6 lg:grid-cols-3">
				  <div class="mb-6 rounded-lg p-6 cursor-pointer hover:shadow-lg transition-shadow" 
					   :style="{ backgroundColor: getBackgroundColor(work.status) }" 
					   v-for="work in getCompletedWorks"
					   :key="work.workId"
					   @click="view(work.workId)">
					<div class="flex items-center justify-between">
					  <div class="flex items-center">
						<h3 class="text-base font-semibold text-gray-900">{{ work.title }}</h3>
					  </div>
					  <div class="flex items-end">
						<svg t="1710908184286" viewBox="0 0 1024 1024" version="1.1"
							 xmlns="http://www.w3.org/2000/svg" p-id="5178" class="mr-2 h-5 w-5 text-base text-gray-500">
						  <path
							d="M512 1003.52a497.92 497.92 0 1 1 497.92-497.92A498.56 498.56 0 0 1 512 1003.52zM512 71.68a433.92 433.92 0 1 0 433.92 433.92A434.56 434.56 0 0 0 512 71.68z"
							fill="#323333" p-id="5179"></path>
						  <path
							d="M152.96 369.92a33.92 33.92 0 0 1 35.2-36.48 39.04 39.04 0 0 1 29.44 16l148.48 198.4V369.92a35.2 35.2 0 1 1 69.76 0V640a30.72 30.72 0 0 1-34.56 33.28 36.48 36.48 0 0 1-29.44-12.8l-147.2-198.4V640a30.72 30.72 0 0 1-34.56 33.28 31.36 31.36 0 0 1-37.12-33.28zM463.36 504.32a162.56 162.56 0 1 1 323.84 0 158.08 158.08 0 0 1-161.92 168.96 159.36 159.36 0 0 1-161.92-168.96z m252.16 0c-3.84-69.12-33.92-104.96-90.24-108.8s-84.48 39.68-88.32 108.8 33.28 104.32 88.32 108.16 86.4-36.48 90.24-108.16zM856.96 603.52A37.12 37.12 0 0 1 896 640c0 21.12-13.44 32-36.48 33.28s-35.84-12.16-37.12-33.28a37.76 37.76 0 0 1 34.56-36.48z"
							fill="#323333" p-id="5180"></path>
						</svg>
						<span class="mr-1">{{ work.workId }}</span>
					  </div>
					</div>
					<p class="my-3 text-sm font-normal text-gray-500 flex items-center">
					  <svg t="1730975355744" class="mr-1 h-5 w-6 text-yellow-500" viewBox="0 0 1024 1024" version="1.1"
						 xmlns="http://www.w3.org/2000/svg" p-id="4273" width="128" height="128">
						   <path d="M954.88 237.568c0-60.416-56.832-109.568-126.464-109.568H195.584c-70.144 0-126.464 49.152-126.464 109.568v548.352c0 60.416 56.832 109.568 126.464 109.568h632.832c70.144 0 126.464-49.152 126.464-109.568V237.568z m-803.84-28.16c11.776-10.24 28.16-16.384 45.056-15.872h632.832c34.816 0 63.488 14.336 63.488 44.544v246.784h-50.176l-54.272-47.104c-12.288-10.752-32.256-10.752-44.544 0-3.584 3.072-6.144 6.656-7.68 10.752l-38.912 101.888L604.16 390.144c-7.68-13.312-26.624-18.944-42.496-12.288-7.168 3.072-12.288 8.192-15.36 14.336l-92.16 199.68-101.376-307.2c-4.608-14.336-22.528-23.04-38.912-18.944-8.704 2.048-15.872 7.168-19.456 14.336l-117.76 204.288H133.12V247.808c-1.024-14.336 6.144-28.16 17.92-38.4z m725.504 611.84c-7.68 7.68-31.232 9.728-48.128 9.728H195.584c-16.896 0-32.768-5.632-45.056-15.872s-18.432-24.064-18.432-38.912v-236.544h63.488c11.776 0 23.04-5.632 28.16-15.36l90.112-156.16 104.448 316.416c3.584 11.264 15.36 19.456 28.672 19.968h1.536c12.8 0 24.576-6.656 29.184-17.408L578.56 468.992l95.232 164.864c7.68 13.312 26.624 18.944 42.496 12.288 7.68-3.072 13.312-8.704 15.872-15.872l47.104-122.368 27.136 23.552c6.144 5.12 13.824 8.192 22.528 8.192h62.976v236.544c0 14.336-9.728 40.448-14.336 44.544l-1.024 0.512z" 
							 p-id="4274">
							</path>
					</svg>
					<span class="mr-2">状态：</span>
					<el-tag
					  :type="getStatusType(work.status)"
					  class="ml-1"
					>
					  {{ work_status.find(item => item.value === String(work.status))?.label }}
					</el-tag>
				  </p>
				  <p class="my-3 text-sm font-normal text-gray-500 flex items-center">
					<svg t="1730967494215" class="mr-1 h-5 w-6 text-yellow-500" viewBox="0 0 1024 1024" version="1.1"
						   xmlns="http://www.w3.org/2000/svg" p-id="934" width="128" height="128">
						   <path d="M85.333333 227.555556A85.333333 85.333333 0 0 1 170.666667 142.222222h682.666666A85.333333 85.333333 0 0 1 938.666667 227.555556v170.666666a28.444444 28.444444 0 0 1-28.444445 28.444445H113.777778a28.444444 28.444444 0 0 1-28.444445-28.444445V227.555556zM170.666667 199.111111a28.444444 28.444444 0 0 0-28.444445 28.444445v142.222222h739.555556V227.555556a28.444444 28.444444 0 0 0-28.444445-28.444445H170.666667z" p-id="935"></path><path d="M256 85.333333A28.444444 28.444444 0 0 1 284.444444 113.777778v113.777778a28.444444 28.444444 0 0 1-56.888888 0V113.777778a28.444444 28.444444 0 0 1 28.444444-28.444445z m512 0a28.444444 28.444444 0 0 1 28.444444 28.444445v113.777778a28.444444 28.444444 0 0 1-56.888888 0V113.777778a28.444444 28.444444 0 0 1 28.444444-28.444445zM711.111111 540.444444a170.666667 170.666667 0 1 0 0 341.333334 170.666667 170.666667 0 0 0 0-341.333334z m-227.555555 170.666667a227.555556 227.555556 0 1 1 455.111111 0 227.555556 227.555556 0 0 1-455.111111 0z" p-id="936"></path><path d="M682.666667 597.333333a28.444444 28.444444 0 0 1 28.444444 28.444445v85.333333H796.444444a28.444444 28.444444 0 0 1 0 56.888889h-113.777777a28.444444 28.444444 0 0 1-28.444445-28.444444v-113.777778a28.444444 28.444444 0 0 1 28.444445-28.444445zM113.777778 369.777778a28.444444 28.444444 0 0 1 28.444444 28.444444v426.666667a28.444444 28.444444 0 0 0 28.444445 28.444444h426.666666a28.444444 28.444444 0 0 1 0 56.888889H170.666667a85.333333 85.333333 0 0 1-85.333334-85.333333V398.222222a28.444444 28.444444 0 0 1 28.444445-28.444444z" p-id="937"></path><path d="M199.111111 540.444444A28.444444 28.444444 0 0 1 227.555556 512h199.111111a28.444444 28.444444 0 0 1 0 56.888889H227.555556a28.444444 28.444444 0 0 1-28.444445-28.444445z m0 170.666667A28.444444 28.444444 0 0 1 227.555556 682.666667h85.333333a28.444444 28.444444 0 0 1 0 56.888889H227.555556a28.444444 28.444444 0 0 1-28.444445-28.444445z" p-id="938">
						</path>
					  </svg>
					  <span>有效期：{{ parseDate(work.createdAt) }} - {{ parseDate(work.deadline) }}</span>
				  </p>
				  <div class="mt-6 flex items-center justify-between text-sm font-semibold text-gray-900">
					<div class="flex items-center">
					  <svg t="1730968056941" 
						   class="mr-1 h-5 w-6 text-yellow-500" 
						   viewBox="0 0 1024 1024" 
						   version="1.1"
						   xmlns="http://www.w3.org/2000/svg" 
						   p-id="4270" 
						   width="128" 
						   height="128">
						<path d="M319.968 960c-5.92 0-11.904-1.664-17.184-4.992-12.672-8.064-18.016-23.84-12.896-37.952L402.304 608 160 608c-12.928 0-24.608-7.776-29.568-19.744s-2.208-25.728 6.944-34.88l480-480c12.512-12.512 32.736-12.512 45.248 0s12.512 32.736 0 45.248L237.248 544 448 544c10.432 0 20.224 5.088 26.208 13.664 6.016 8.544 7.424 19.456 3.872 29.28l-78.72 216.448L786.752 416 576 416c-17.696 0-32-14.336-32-32s14.304-32 32-32l288 0c12.928 0 24.64 7.808 29.568 19.744 4.96 11.968 2.208 25.728-6.944 34.88l-544 544C336.448 956.8 328.224 960 319.968 960z" 
							  fill="#d81e06" 
							  p-id="4271"/>
					  </svg>
					  <span class="mr-2">级别：</span>
					  <!-- type["primary", "success", "info", "warning", "danger"] -->
					  <el-tag
						:type="work.priority === '紧急' ? 'danger' : 'primary'" 
						class="ml-1"
						>
						{{ work.priority }}
					  </el-tag>
					</div>
					<div class="flex items-center">
  
					  <el-button class="!p-0" link icon="View"  @click="view(work.workId)" text
								 type="primary"
					  >查看
					  </el-button>
					  <!-- <el-button class="!p-0" icon="edit-pen" @click="formDialogRef.openDialog(work.workId)" text
								 type="primary"
								 v-auth="'rs_work_edit'"
					  >编辑
					  </el-button> -->
					  <!-- <el-button
						  class="!p-0"
						  icon="delete"
						  :disabled="work.workId === '1'"
						  @click="handleDelete([work.workId])"
						  text
						  type="primary"
						  v-auth="'rs_work_del'"
					  >删除
					  </el-button> -->
					</div>
				  </div>
				</div>
			  </div>
			</div>
		  </div>
		</el-scrollbar>
  
		<pagination @current-change="currentChangeHandle" @size-change="sizeChangeHandle"
					v-bind="state.pagination"/>
	  </div>
  
	  <!-- 编辑、新增  -->
	  <form-dialog @refresh="getDataList()" ref="formDialogRef"/>
  
	  <!-- 编辑、新增  -->
	  <!-- <individuation ref="individuationRef"/> -->
  
	  <!-- 导入excel -->
	  <upload-excel
			ref="excelUploadRef"
			title="导入"
			url="/rs/work/import"
			temp-url="/admin/sys-file/local/file/work.xlsx"
			@refreshDataList="getDataList"
		/>
	</div>
  </template>
  
  <script lang="ts" name="workPending" setup>
  import {BasicTableProps, useTable} from '/@/hooks/table';
//   import {delObj, fetchPage,fetchList} from '/@/api/admin/tenant';
  import { WorkfetchList, delObjs, getObj } from '/@/api/rs/work';
  import {useMessage, useMessageBox} from '/@/hooks/message';
  import {useI18n} from 'vue-i18n';
  import {useDict} from '/@/hooks/dict';
  import { pageRoleList } from '/@/api/admin/user';
  import { useUserInfo } from '/@/stores/userInfo'; // 引入用户信息
  
  // 引入组件
  const FormDialog = defineAsyncComponent(() => import('./form.vue'));
  const {t} = useI18n();
  // 定义对象的类型
  interface Users {
	isAdmin: string;
	userId: string;
	name: string;
  }
  // 定义变量内容
  const formDialogRef = ref();
  const individuationRef = ref();
  const excelUploadRef = ref();
  // 搜索变量
  const queryRef = ref();
  const showSearch = ref(true);
  // 多选变量
  const selectObjs = ref([]) as any;
  const multiple = ref(true);
  
  // 字典
  const { work_status } = useDict('work_status');
  const { priority } = useDict('priority');
  
  const state: BasicTableProps = reactive<BasicTableProps>({
	queryForm: {},
	dataList: [], // 用于存储获取到的数据
  });
  
  //  table hook
  const {currentChangeHandle, sizeChangeHandle, downBlobFile, tableStyle} = useTable(state);
  const getDataList = () => loadData();
  // 清空搜索条件
  const resetQuery = () => {
	queryRef.value.resetFields();
	getDataList();
  };
  
  // 导出excel
  const exportExcel = () => {
	downBlobFile('/rs/work/export', Object.assign(state.queryForm, {ids: selectObjs}), 'work.xlsx');
  };


  // 获取当前用户信息
const currentUserId = ref('');
const currentUserName = ref('');
const fetchCurrentUser = async () => {
	const data = useUserInfo().userInfos;
	currentUserName.value = data.user.name;
	currentUserId.value = data.user.userId; // 确保正确设置当前用户ID
};
  const users = ref<Users[]>([]);

  const fetchUsers = async () => {
	try {
		const response = await pageRoleList();
		users.value = response.data.records; // 假设返回的数据结构中用户列表在`records`字段中
	} catch (error) {
		console.error('Failed to fetch users:', error);
	}
   };

    // 定义获取数据的函数
  const loadData = async () => {
	state.loading = true;
	if (!state.queryForm.assignees) {
			state.queryForm.assignees = currentUserId.value;
		}
	try {
		await WorkfetchList(state.queryForm).then((res) => {
			state.dataList = res.data.data;
			state.pagination = res.data.page;
			// console.log('111', state.pagination);
		});

		// state.dataList = response.data; // 假设返回的数据在 response.data 中
		// console.log('111111', state.dataList);
	} catch (error) {
		console.error('Error loading data:', error);
	} finally {
		state.loading = false;
	}
  };
  
  // 在组件挂载时获取数据
  onMounted(async () => {
	await fetchCurrentUser(); // 先获取当前用户信息
	loadData(); // 然后加载数据
	fetchUsers();
});

  // 删除操作
  const handleDelete = async (ids: string[]) => {
	try {
	  await useMessageBox().confirm(t('此操作将永久删除'));
	} catch {
	  return;
	}
  
	try {
	  await delObjs(ids);
	  getDataList();
	  useMessage().success(t('删除成功'));
	} catch (err: any) {
	  useMessage().error(err.msg);
	}
  };

  const view = (id: any) => {
	// 打开表单对话框,传入只读模式参数
	formDialogRef.value?.openDialog(id); 
};

  const getStatusType = (status: number) => {
	switch (status) {
		case 0:
			//处待理
			return 'danger';
		case 1:
			//处理中
			return 'success';
		case 2:
			//已处理
			return 'info';
		case 3:
			//已终止
			return 'info';
		default:
			return 'primary';
	}
  };

  // 添加获取背景色的方法
  const getBackgroundColor = (status: number) => {
    switch (status) {
      case 0: // 待处理
      case 1: // 处理中
        return '#ffebee'; // 浅红色背景
      default:
        return 'var(--el-fill-color-light)'; // 默认背景色
    }
  };

  // 添加计算属性来过滤工单
  const getPendingWorks = computed(() => {
	return state.dataList.filter(work => work.status === 0 || work.status === 1);
  });

  const getCompletedWorks = computed(() => {
	return state.dataList.filter(work => work.status === 2);
  });
  </script>
  
  <style scoped>
  .mb-8 {
	margin-bottom: 2rem;
  }

  .text-xl {
	font-size: 1.25rem;
	line-height: 1.75rem;
  }

  .font-bold {
	font-weight: 700;
  }
  </style>
  