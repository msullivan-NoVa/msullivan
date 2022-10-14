import React, {Component, Components} from 'react'

import Title from './Title'
import PlatformSelector from './PlatformSelector'
// import PlatformInterigator from './PlatformInterigator';

const platforms = [
  {
    vehicleId: "abc123",
  }, {
    vehicleId: "def456"
  }, {
    vehicleId: "jkl789"
  }
]

class Main extends Component {
  constructor(props) {
    super(props);
    this.handleConnectClick = this.handleConnectClick.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.state = {selectedPlatform: '', selectedPlatformChanged: false};
  }

  handleConnectClick() {
    this.setState({selectedPlatformChanged: false});
    const text = "connected to " + this.state.selectedPlatform;
    console.log(text)
    console.log("read status through web api")
    console.log("get faults through web api")
  }

  handleChange(event) {
    console.log({event});
    this.setState({selectedPlatform: event.value});
    this.setState({selectedPlatformChanged: true});
    console.log("changed {this.state.selectedPlatform}")
  } 

  render() {
    return <div>
      <Title title={"Maintenance Interface"}/>
      <PlatformSelector platforms={platforms} selectedPlatform={this.selectedPlatform} onChange={this.handleChange}/>
      <div><button disabled={!this.state.selectedPlatformChanged} type='input' onClick={this.handleConnectClick}>Connect {this.state.selectedPlatform}</button></div>
      {/* <PlatformInterigator selectedPlatform={this.state.selectedPlatform} />  */}
    </div>
  }
}

export default Main