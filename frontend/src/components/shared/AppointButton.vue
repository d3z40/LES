<template>
  <v-btn small
    :disabled="disable"
    outline
    flat
    color="primary" 
    @click="appoint()">
    {{ buttonName }}
  </v-btn>
</template>

<script>
export default {
  props: ['buttonName', 'disable'],
  methods: {
    appoint () {
      this.$emit('emitAppoint', this.buttonName)
    }
  }
}
</script>
