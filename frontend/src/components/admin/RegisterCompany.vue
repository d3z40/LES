<template>
    <div>
    <h1>Dados da Empresa</h1>
    <li v-for="(message, index) in messages" :key="index">
      <v-alert :color="messageColor"
               :value="haveMessage"
               v-text="message"
               transition="scale-transition" />
    </li>
    <br/>
    <v-form ref="form" v-model="valid">

      <v-layout row wrap>
      <!-- Row 1 -->
        <v-flex xs6>
          <v-text-field v-model="company.socialName" id="socialName"
                :rules="$v_company.socialNameRules(company.socialName)"
                type="text"
                class="px-0"
                prepend-icon="assignment"
                clearable
                label="Razão Social"
                required>
          </v-text-field>
        </v-flex>
        <v-flex xs6>
          <v-text-field v-model="company.tradingName" id="tradingName"
                :rules="$v_company.tradingNameRules(company.tradingName)"
                type="text"
                prepend-icon="rate_review"
                clearable
                label="Nome Fantasia"
                required>
          </v-text-field>
        </v-flex>
        <!-- Row 2  for testes 61442737000230 -->
        <v-flex xs6>
          <v-text-field v-model="company.cnpj" id="cnpj"
                :rules="$v_company.cnpjRules(company.cnpj)"
                :counter="14"
                type="text"
                prepend-icon="email"
                clearable
                label="CNPJ"
                required>
          </v-text-field>
        </v-flex>
        <v-flex xs6>
          <!-- for tests 454504330118 -->
          <v-text-field v-model="company.stateRegistration" id="stateRegistration"
                :rules="$v_company.stateRegistrationRules(company.stateRegistration)"
                :counter="12"
                type="text"
                prepend-icon="filter_frames"
                clearable
                label="Inscrição Estadual"
                required>
          </v-text-field>
        </v-flex>
        <!-- row 3 for tests 08750710 -->
        <v-flex xs4>
          <v-text-field id="zipCode"
                v-model="company.address.zipCode"
                :rules="$v_address.zipCodeRules(company.address.zipCode)"
                :counter="8"
                type="text"
                prepend-icon="event_note"
                clearable
                label="CEP"
                required
                @change="getAddress">
          </v-text-field>
        </v-flex>
        <v-flex xs4>
          <v-text-field id="number"
                        v-model="company.address.number"
                        :rules="$v_address.numberRules(company.address.number)"
                        type="number"
                        prepend-icon="filter_1"
                        clearable
                        label="Número"
                        required>
          </v-text-field>
        </v-flex>
        <v-flex xs4>
          <v-text-field id="street"
                        v-model="company.address.street"
                        :rules="$v_address.streetRules(company.address.street)"
                        type="text"
                        prepend-icon="place"
                        clearable
                        label="Rua"
                        required>
          </v-text-field>
        </v-flex>
        <!-- row 4 -->
        <v-flex xs6>
          <v-text-field id="city"
                        v-model="company.address.city"
                        :rules="$v_address.cityRules(company.address.city)"
                        type="text"
                        prepend-icon="domain"
                        clearable
                        label="Cidade"
                        required>
          </v-text-field>
        </v-flex>
        <v-flex xs6>
          <v-text-field id="state"
                        v-model="company.address.state"
                        :rules="$v_address.stateRules(company.address.state)"
                        type="text"
                        prepend-icon="location_city"
                        clearable
                        label="Estado"
                        required>
          </v-text-field>
        </v-flex>
      </v-layout>

      <v-flex xs12 right>
        <v-btn id="success" @click="submit"
              :disabled="!valid"
              color="success">Salvar</v-btn>
        <v-btn @click="clear" id="cancel" color="error">Cancelar</v-btn>
      </v-flex>

    </v-form>
      <br/><br/><br/><br/>
      <CompanyList @emitCompany="updateCompany($event)" ref="companyList"></CompanyList>
  </div>
</template>

<script>
import CompanyList from '@/components/admin/CompanyList'
export default {
  data: function () {
    return {
      company: {
        id: 0,
        address: {}
      },
      edit: false,
      haveMessage: false,
      messages: [],
      messageColor: '',
      valid: false
    }
  },
  components: {
    CompanyList
  },
  methods: {
    submit () {
      console.log(JSON.stringify(this.company))
      if (this.company.id === 0) {
        this.$_axios.post(`${this.$_url}company`, this.company).then(response => {
          console.log('post')
          let result = response.data
          if (result.resultList.length !== 0) {
            this.company = result.resultList[0]
          }
          if (result.message) {
            this.messages = [...result.message]
            this.haveMessage = true
            if (result.success) {
              this.messageColor = 'info'
              this.clear()
              this.company = {
                id: 0,
                address: {}
              }
              this.$refs.companyList.initialize()
            } else {
              this.messageColor = 'warning'
            }
          }
        }).catch(error => {
          console.log(error)
          this.messages = ['Erro durante execução do serviço!']
          this.haveMessage = true
          this.messageColor = 'error'
        })
      } else {
        this.$_axios.put(`${this.$_url}company`, this.company).then(response => {
          console.log('put')
          let result = response.data
          if (result.resultList.length !== 0) {
            this.company = result.resultList[0]
          }
          if (result.message) {
            this.messages = [...result.message]
            this.haveMessage = true
            if (result.success) {
              this.messageColor = 'info'
              this.clear()
              this.company = {
                id: 0,
                address: {}
              }
              this.$refs.companyList.initialize()
            } else {
              this.messageColor = 'warning'
            }
          }
        }).catch(error => {
          console.log(error)
          this.messages = ['Erro durante execução do serviço!']
          this.haveMessage = true
          this.messageColor = 'error'
        })
      }
    },
    clear () {
      this.$refs.form.reset()
    },
    updateCompany (companyToEdit) {
      console.log(JSON.stringify(companyToEdit))
      this.company = companyToEdit
    },
    getAddress (zipCode) {
      if (zipCode.length === 8) {
        this.$_axios.get(`${this.$_viaCep}${zipCode}/json`).then(reponse => {
          let address = reponse.data
          this.company.address.street = address.logradouro
          this.company.address.city = address.localidade
          this.company.address.state = address.uf
        }).catch(error => {
          console.log(error)
          if (error.data) {
            this.messages = ['CEP não encontrado!']
            this.haveMessage = true
            this.messageColor = 'error'
          }
        })
      }
    }
  }
}
</script>

<style scoped>
h1 {
  font-size: 40px;
  text-align: center;
  margin-top: -20px;
  margin-bottom: 20px;
}
</style>
