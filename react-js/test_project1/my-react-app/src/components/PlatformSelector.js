import React, {Component, Components, useState} from 'react'
import Select from 'react-select';

export const PlatformSelector = (props) => {
  const platformList = props.platforms.map(platform => ({ label: platform.vehicleId, value: platform.vehicleId }));
  
  return (
    <div>
       <Select
        value={props.selectedPlatform}
        options={platformList}        
        onChange={props.onChange}
        />
    </div>
  );
}

//  Select also has an AsyncSelect, only loads when interacted with
export default PlatformSelector