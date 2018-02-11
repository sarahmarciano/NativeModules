import React, { Component } from 'react';
import {
  StyleSheet,
  NativeModules,
  Text,
  View,
  Image,
  FlatList,
  DeviceEventEmitter
} from 'react-native';

const ContactsManager = NativeModules.ContactsManager;

export default class App extends Component {
  constructor(props) {
    super(props);

    this.state = { contacts: [] };
  }
  componentWillMount() {
    ContactsManager.getContacts();
    DeviceEventEmitter.addListener('aaa', function (e) {
      this.setState({ contacts: [...this.state.contacts, e] });
    }.bind(this));
  }


  renderItem(contact) {
    const data = contact.split('*');
    const { itemStyle, textStyle, imageStyle } = styles;
    return (
      <View style={itemStyle}>
        {data[2] !== 'null' && <Image source={{ uri: data[2] }} style={[imageStyle, { borderRadius: 25 }]} />}
        {data[2] === 'null' && <Image source={require('./contact.png')} style={imageStyle} />}
        <View style={{ marginLeft: 10 }}>
          <Text style={textStyle}>{data[0]}</Text>
          <Text>{data[1]}</Text>
        </View>
      </View>
    );
  }
  render() {
    const { contacts } = this.state;
    const { headerStyle, titleStyle } = styles;
    return (
      <View>
        <View style={headerStyle}>
          <Text style={titleStyle}>Contacts</Text>
        </View>
        <FlatList
          data={contacts.sort()}
          renderItem={({ item }) => this.renderItem(item)}
          keyExtractor={(item, index) => index}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  itemStyle: {
    flexDirection: 'row',
    alignItems: 'center',
    borderColor: '#ccc',
    borderBottomWidth: 1,
    padding: 10,
    flex: 1
  },
  textStyle: {
    fontSize: 18,
    flex: 1,
    flexWrap: 'wrap'
  },
  imageStyle: {
    width: 50,
    height: 50
  },
  headerStyle: {
    height: 60,
    alignItems: 'center',
    justifyContent: 'center',
    elevation: 3,
    backgroundColor: '#e93636'
  },
  titleStyle: {
    fontWeight: 'bold',
    fontSize: 19,
    color: 'white'
  }
});
