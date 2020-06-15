FROM postman/newman:ubuntu

COPY . /etc/newman

RUN chmod +x wait-for-it.sh
RUN chmod a+x /etc/newman/*.sh

ENTRYPOINT ["/etc/newman/entrypoint.sh"]