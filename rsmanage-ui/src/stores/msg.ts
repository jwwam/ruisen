import { defineStore } from 'pinia';

interface Message {
	label: string;
	value: string;
	type: string;
	time: string;
}

export const useMsg = defineStore('msg', {
	state: () => ({
		messages: [] as Message[]
	}),
	
	actions: {
		handleWebSocketMsg(msg: any) {
			const newMsg: Message = {
			  label: msg.title || msg.taskName || '系统通知111',
			  value: msg.message || msg.content,
			  type: msg.status || msg.type || 'info',
			  time: msg.executionTime
			};
			
			this.messages.unshift(newMsg);
			
			if (this.messages.length > 50) {
			  this.messages = this.messages.slice(0, 50);
			}
		},
		
		getAllMsg() {
			return this.messages;
		},
		
		removeMsg(index: number) {
			if (index >= 0 && index < this.messages.length) {
				this.messages = this.messages.filter((_, i) => i !== index);
				return true;
			}
			return false;
		},
		
		removeAll() {
			this.messages = [];
		}
	},
	
	persist: {
		enabled: true,
		strategies: [
			{
				key: 'msg',
				storage: localStorage
			}
		]
	}
});
