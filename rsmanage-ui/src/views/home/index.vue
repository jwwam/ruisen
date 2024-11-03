<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <splitpanes>
        <pane size="70">
          <splitpanes horizontal>
            <pane size="25">
              <splitpanes>
                <pane size="50">
                  <current-user/>
                </pane>
                <pane size="50">
                  <flow-data/>
                </pane>
              </splitpanes>
            </pane>
            <pane size="75">
              <favorite/>
            </pane>
          </splitpanes>
        </pane>
        <pane size="30">
          <splitpanes horizontal>
            <pane size="100">
              <schedule/>
            </pane>
            <!-- <pane size="42">
              <sys-log/>
            </pane> -->
          </splitpanes>
        </pane>
      </splitpanes>
    </div>
  </div>
</template>

<script setup lang="ts" name="home">
import { split } from 'postcss/lib/list';

const CurrentUser = defineAsyncComponent(() => import('./current-user.vue'));
const Favorite = defineAsyncComponent(() => import('./favorite.vue'));
const Schedule = defineAsyncComponent(() => import('./schedule.vue'));
// const SysLog = defineAsyncComponent(() => import('./sys-log.vue'));
const flowData = defineAsyncComponent(() => import('./flow-data.vue'));
</script>
