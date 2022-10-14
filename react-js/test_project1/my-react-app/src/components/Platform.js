import React, {Component} from 'react'

class Platform extends Component {
  render() {
    const vehicleId = this.props.platform.vehicleId
    return <a href="#{vehicleId}">{vehicleId}</a>
  }
}

export default Platform
